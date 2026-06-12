package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.OverpassBoundingBox;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.OverpassImportResult;

@Service
public class OverpassImportService {

    private static final Logger logger = LoggerFactory.getLogger(OverpassImportService.class);

    private static final String INSERT_POLYGON_SQL = """
        INSERT INTO planet_osm_polygon (osm_id, building, highway, name, tags, way)
        VALUES (?, ?, ?, ?, ?::jsonb,
            ST_Multi(ST_Transform(ST_SetSRID(ST_GeomFromGeoJSON(?), 4326), 3857)))
        ON CONFLICT (osm_id) DO NOTHING
        """;

    private static final String INSERT_LINE_SQL = """
        INSERT INTO planet_osm_line (osm_id, highway, name, tags, way)
        VALUES (?, ?, ?, ?, ?::jsonb,
            ST_Transform(ST_SetSRID(ST_GeomFromGeoJSON(?), 4326), 3857))
        ON CONFLICT (osm_id) DO NOTHING
        """;

    private static final String RAW_POLYGON_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS planet_osm_polygon (
            osm_id BIGINT PRIMARY KEY,
            building TEXT,
            highway TEXT,
            name TEXT,
            tags JSONB,
            way geometry(MultiPolygon, 3857)
        );
        CREATE INDEX IF NOT EXISTS idx_planet_osm_polygon_way ON planet_osm_polygon USING GIST (way);
        """;

    private static final String RAW_LINE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS planet_osm_line (
            osm_id BIGINT PRIMARY KEY,
            highway TEXT,
            name TEXT,
            tags JSONB,
            way geometry(LineString, 3857)
        );
        CREATE INDEX IF NOT EXISTS idx_planet_osm_line_way ON planet_osm_line USING GIST (way);
        """;

    private final JdbcTemplate jdbcTemplate;
    private final String overpassApiUrl;
    private final Duration timeout;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Autowired
    public OverpassImportService(JdbcTemplate jdbcTemplate,
            @Value("${overpass.api.url:https://overpass-api.de/api/interpreter}") String overpassApiUrl,
            @Value("${overpass.connect.timeout.seconds:30}") int timeoutSeconds) {
        this.jdbcTemplate = jdbcTemplate;
        this.overpassApiUrl = overpassApiUrl;
        this.timeout = Duration.ofSeconds(timeoutSeconds);
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(this.timeout)
                .build();
    }

    @PostConstruct
    public void ensureRawTablesExist() {
        logger.info("Ensuring Overpass raw tables exist...");
        try {
            String polygonTagsType = getColumnUdtName("planet_osm_polygon", "tags");
            if (polygonTagsType != null && !polygonTagsType.equalsIgnoreCase("jsonb")) {
                logger.info("Detected existing planet_osm_polygon.tags of type {} - recreating as jsonb", polygonTagsType);
                jdbcTemplate.execute("DROP TABLE IF EXISTS planet_osm_polygon CASCADE");
                jdbcTemplate.execute(RAW_POLYGON_TABLE_SQL);
            } else {
                jdbcTemplate.execute(RAW_POLYGON_TABLE_SQL);
            }

            String lineTagsType = getColumnUdtName("planet_osm_line", "tags");
            if (lineTagsType != null && !lineTagsType.equalsIgnoreCase("jsonb")) {
                logger.info("Detected existing planet_osm_line.tags of type {} - recreating as jsonb", lineTagsType);
                jdbcTemplate.execute("DROP TABLE IF EXISTS planet_osm_line CASCADE");
                jdbcTemplate.execute(RAW_LINE_TABLE_SQL);
            } else {
                jdbcTemplate.execute(RAW_LINE_TABLE_SQL);
            }
        } catch (DataAccessException e) {
            // Fallback: try to create tables if detect failed
            logger.warn("Could not detect existing tags column type, attempting to create tables: {}", e.getMessage());
            jdbcTemplate.execute(RAW_POLYGON_TABLE_SQL);
            jdbcTemplate.execute(RAW_LINE_TABLE_SQL);
        }
    }

    private String getColumnUdtName(String tableName, String columnName) {
        try {
            String sql = "SELECT udt_name FROM information_schema.columns WHERE table_name = ? AND column_name = ?";
            return jdbcTemplate.queryForObject(sql, new Object[] { tableName, columnName }, String.class);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public OverpassImportResult importBoundingBox(OverpassBoundingBox bbox) throws Exception {
        logger.info("Starting Overpass import for bbox: {}", bbox);
        ensureRawTablesExist();
        emptyPredictionTables();

        JsonNode root = fetchOverpassPayload(bbox);
        List<Object[]> polygonRows = new ArrayList<>();
        List<Object[]> lineRows = new ArrayList<>();

        JsonNode elements = root.path("elements");
        if (!elements.isArray()) {
            throw new IllegalStateException("Unexpected Overpass response: missing elements array");
        }

        for (JsonNode element : elements) {
            if (!"way".equals(element.path("type").asText())) {
                continue;
            }

            long osmId = element.path("id").asLong();
            JsonNode tagsNode = element.path("tags");
            Map<String, String> tags = new HashMap<>();
            if (tagsNode.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = tagsNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    tags.put(field.getKey(), field.getValue().asText());
                }
            }
            String buildingValue = tags.get("building");
            String highwayValue = tags.get("highway");
            String nameValue = tags.get("name");

            JsonNode geometry = element.path("geometry");
            if (!geometry.isArray() || geometry.size() < 2) {
                logger.warn("Skipping way {} because it has no geometry", osmId);
                continue;
            }
            List<List<Double>> coordinates = new ArrayList<>();
            for (JsonNode node : geometry) {
                double lat = node.path("lat").asDouble();
                double lon = node.path("lon").asDouble();
                coordinates.add(List.of(lon, lat));
            }

            if (buildingValue != null) {
                if (coordinates.size() < 4) {
                    logger.warn("Skipping building way {} because coordinates are too few", osmId);
                    continue;
                }
                if (!coordinates.get(0).equals(coordinates.get(coordinates.size() - 1))) {
                    coordinates.add(coordinates.get(0));
                }
                Map<String, Object> polygonGeoJson = Map.of(
                        "type", "Polygon",
                        "coordinates", List.of(coordinates));
                String geometryJson = objectMapper.writeValueAsString(polygonGeoJson);
                String tagsJson = objectMapper.writeValueAsString(tags);
                polygonRows.add(new Object[] {
                        osmId,
                        buildingValue,
                        highwayValue,
                        nameValue,
                        tagsJson,
                        geometryJson
                });
            } else if (highwayValue != null) {
                Map<String, Object> lineGeoJson = Map.of(
                        "type", "LineString",
                        "coordinates", coordinates);
                String geometryJson = objectMapper.writeValueAsString(lineGeoJson);
                String tagsJson = objectMapper.writeValueAsString(tags);
                lineRows.add(new Object[] {
                        osmId,
                        highwayValue,
                        nameValue,
                        tagsJson,
                        geometryJson
                });
            }
        }

        int importedBuildings = 0;
        int importedRoads = 0;
        if (!polygonRows.isEmpty()) {
            int[] polygonCounts = jdbcTemplate.batchUpdate(INSERT_POLYGON_SQL, polygonRows);
            importedBuildings = polygonCounts.length;
        }
        if (!lineRows.isEmpty()) {
            int[] lineCounts = jdbcTemplate.batchUpdate(INSERT_LINE_SQL, lineRows);
            importedRoads = lineCounts.length;
        }

        logger.info("Imported {} buildings and {} roads from Overpass.", importedBuildings, importedRoads);
        return new OverpassImportResult(importedBuildings, importedRoads, 0, 0);
    }

    private JsonNode fetchOverpassPayload(OverpassBoundingBox bbox) throws Exception {
        String query = buildOverpassQuery(bbox);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(overpassApiUrl))
                .timeout(timeout)
                .header("Content-Type", "text/plain; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(query))
                .build();

        try (InputStream input = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream()).body()) {
            return objectMapper.readTree(input);
        }
    }

    private String buildOverpassQuery(OverpassBoundingBox bbox) {
        return String.format(
                "[out:json][timeout:60];\n(" +
                        "way[\"building\"](%f,%f,%f,%f);\n" +
                        "way[\"highway\"~\"trunk|trunk_link|primary|primary_link|secondary|secondary_link|tertiary|tertiary_link|unclassified|residential|service|living_street\"](%f,%f,%f,%f);\n" +
                        ");\nout geom;\n",
                bbox.south(), bbox.west(), bbox.north(), bbox.east(),
                bbox.south(), bbox.west(), bbox.north(), bbox.east());
    }

    private void emptyPredictionTables() {
        // Clear raw import and derived prediction tables to avoid stale data between runs.
        List<String> tables = List.of(
                "planet_osm_polygon",
                "planet_osm_line",
                "cleaned_buildings",
                "cleaned_roads",
                "noded_streets",
                "building_drop_points",
                "network_points",
                "network_connections",
                "linked_buildings"
        );

        for (String table : tables) {
            try {
                jdbcTemplate.execute("TRUNCATE TABLE " + table + " RESTART IDENTITY CASCADE");
                logger.info("Truncated table {}", table);
            } catch (DataAccessException ex) {
                logger.warn("Could not truncate {}: {}", table, ex.getMessage());
            }
        }

        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS noded_streets_vertices_pgr");
            logger.info("Dropped stale topology table noded_streets_vertices_pgr");
        } catch (DataAccessException ex) {
            logger.warn("Could not drop noded_streets_vertices_pgr: {}", ex.getMessage());
        }
    }
}
