# data-processing docker project

This project starts portgres with postgis extensions and imports osm data using an osm2pgsql container which runs once at startup.

# pgadmin4

pgadmin4 server available at http://localhost:8888

username: user-name@domain-name.com
password: minad1234

# Cleaning commands

## Cleaning House Footprints

Cleans up house footprints.
Uses ```ST_MakeValid``` to clean up and put them on a 2D plane. Checks for the tags in the column list and tags column.

```sql
CREATE TABLE cleaned_buildings AS
SELECT 
    osm_id,
    ST_CollectionExtract(ST_MakeValid(ST_Multi(way)), 3)::geometry(MultiPolygon, 3857) as geom,
    COALESCE(tags->'name', 'Building ' || osm_id) as building_name,
    tags->'addr:housenumber' as house_num,
    tags->'addr:street' as street_name,
    tags->'building:levels' as floors
FROM planet_osm_polygon
WHERE building IS NOT NULL;

ALTER TABLE cleaned_buildings ADD PRIMARY KEY (osm_id);

CREATE INDEX idx_buildings_geom ON cleaned_buildings USING GIST (geom);
```

This creates a table called cleaned_buildings. The columns are osm_id, geom, name, house_number, street_name

## Street Nodes

This makes a table for the cleaned roads. 
The roads are simplified by 1 meter.
A node is created at each junction making it so each road starts and ends at a node.

```sql
CREATE TABLE cleaned_roads AS
SELECT 
    osm_id,
    ST_SimplifyPreserveTopology(way, 0.5)::geometry(LineString, 3857) as geom,
    highway as road_type,
    tags->'name' as street_name
FROM planet_osm_line
WHERE highway IS NOT NULL;

CREATE INDEX idx_roads_geom ON cleaned_roads USING GIST (geom);

CREATE TABLE noded_streets AS 
SELECT (ST_Dump(ST_Node(ST_Union(geom)))).geom::geometry(LineString, 3857) as geom
FROM cleaned_roads;

CREATE INDEX idx_noded_streets_geom ON noded_streets USING GIST (geom);
```

## Remove road islands

Sometimes there are roads which don't connect to other roads, these will cause errors when trying to predict infrastructure.

```sql
ALTER TABLE cleaned_roads ADD COLUMN is_island BOOLEAN DEFAULT FALSE;

WITH islands AS (
    SELECT a.osm_id
    FROM cleaned_roads a
    WHERE NOT EXISTS (
        SELECT 1 FROM cleaned_roads b 
        WHERE a.osm_id <> b.osm_id 
        AND ST_DWithin(ST_StartPoint(a.geom), b.geom, 0.1)
    )
    AND NOT EXISTS (
        SELECT 1 FROM cleaned_roads b 
        WHERE a.osm_id <> b.osm_id 
        AND ST_DWithin(ST_EndPoint(a.geom), b.geom, 0.1)
    )
)
UPDATE cleaned_roads 
SET is_island = TRUE 
WHERE osm_id IN (SELECT osm_id FROM islands);

DELETE FROM cleaned_roads WHERE is_island = TRUE;
```

## Fix missing street names

This will fill in missing street names based on the closted street.

```sql
UPDATE cleaned_buildings b
SET street_name = (
    SELECT r.street_name 
    FROM cleaned_roads r 
    WHERE r.street_name IS NOT NULL
    ORDER BY b.geom <-> r.geom
    LIMIT 1
)
WHERE b.street_name IS NULL;
```

## Creating connection points

This will create points to connect the cables too, by using the closest point in the building to the road.

```sql
CREATE TABLE building_drop_points AS
SELECT 
    b.osm_id as building_id,
    NULL::BIGINT AS parent_id,
    ST_ClosestPoint(ST_ExteriorRing(ST_GeometryN(b.geom, 1)), r.geom) as geom
FROM cleaned_buildings b
CROSS JOIN LATERAL (
    SELECT geom FROM cleaned_roads 
    ORDER BY b.geom <-> geom 
    LIMIT 1
) r;

ALTER TABLE building_drop_points ADD PRIMARY KEY (building_id);

CREATE INDEX idx_building_drop_points_geom ON building_drop_points USING GIST (geom);
```

## Check commands worked

```sql
SELECT 
    count(*) as total_buildings,
    count(street_name) as buildings_with_street
FROM cleaned_buildings;
```

## Adding Tables for Infrastructure Prediction
```sql
CREATE TABLE network_points (
    id        BIGSERIAL PRIMARY KEY,
    external_id VARCHAR,
    parent_id BIGINT,
    type      VARCHAR,
    geom      geometry(Point, 3857)
);

CREATE INDEX idx_network_points_geom ON network_points USING GIST (geom);

CREATE TABLE network_connections (
    id          BIGSERIAL PRIMARY KEY,
    external_id VARCHAR,
    start_id    BIGINT,
    end_id      BIGINT,
    link_type   VARCHAR,
    geom        geometry(LineString, 3857)
);

CREATE INDEX idx_network_connections_geom ON network_connections USING GIST (geom);

-- Indexing the type column on network points

CREATE INDEX idx_network_points_type ON network_points (type);
```

A full version that can me copied and pasted into the pgadmin query tool 
can be found [here](./full-tables-creation-query.md)

If `docker compose up -d` has been run but there is no osm tables, rerun the importer command:
```bash
docker compose run importer
```

# UPRN Linking
uprn-linker folder contains a java file that performas a spatial join. It imports the uprn.csv file then maps it to the building footprints. 
The results can be reviried by checking the final_linked_network table and using this SQL statement.

```sql
SELECT 
    count(uprn) as matched_uprns,
    round(count(uprn)::numeric / (SELECT count(*) FROM cleaned_buildings)::numeric * 100, 2) || '%' as coverage
FROM final_linked_network;
```

Building with multiple households in can be identified with this

```sql
SELECT osm_id, count(uprn) as units
FROM final_linked_network
GROUP BY osm_id HAVING count(uprn) > 1;
```