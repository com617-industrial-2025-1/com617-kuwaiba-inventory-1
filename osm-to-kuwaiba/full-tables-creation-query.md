# Full Tables Creation Query

The following expands on the data processing teams queries to create the tables ready for
infrastructure prediction.

```sql
-- Cleaning House Footprints

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

-- Street Nodes

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

-- Remove Road Islands

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

-- Fix Missing Street Names

UPDATE cleaned_buildings b
SET street_name = (
    SELECT r.street_name 
    FROM cleaned_roads r 
    WHERE r.street_name IS NOT NULL
    ORDER BY b.geom <-> r.geom
    LIMIT 1
)
WHERE b.street_name IS NULL;

-- Creating Connection Points

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

-- Creating Network Tables

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