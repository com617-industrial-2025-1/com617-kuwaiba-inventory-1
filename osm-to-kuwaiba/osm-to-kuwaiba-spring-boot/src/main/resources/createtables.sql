-- schema.sql creates all the tables and then this is run complete data preparation

INSERT INTO cleaned_buildings (osm_id, geom, building_name, house_num, street_name, floors)
SELECT 
    osm_id,
    ST_CollectionExtract(ST_MakeValid(ST_Multi(way)), 3)::geometry(MultiPolygon, 3857),
    COALESCE(tags->>'name', 'OSMID_' || osm_id), -- changed from Building 
        tags->>'addr:housenumber',
        tags->>'addr:street',
        tags->>'building:levels'
FROM planet_osm_polygon
WHERE building IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM cleaned_buildings LIMIT 1)
ON CONFLICT (osm_id) DO NOTHING;

INSERT INTO cleaned_roads (osm_id, geom, road_type, street_name)
SELECT 
    osm_id,
    ST_SimplifyPreserveTopology(way, 0.5)::geometry(LineString, 3857),
    highway,
    name
FROM planet_osm_line
WHERE highway IN (
    'trunk',
    'trunk_link',
    'primary',
    'primary_link',
    'secondary',
    'secondary_link',
    'tertiary',
    'tertiary_link',
    'unclassified',
    'residential',
    'service',
    'living_street'
)
AND NOT EXISTS (SELECT 1 FROM cleaned_roads LIMIT 1)
ON CONFLICT (osm_id) DO NOTHING;

-- Remove road islands
WITH connected_roads AS (
    SELECT DISTINCT a.osm_id
    FROM cleaned_roads a
    JOIN cleaned_roads b ON a.osm_id <> b.osm_id
    WHERE ST_DWithin(ST_StartPoint(a.geom), b.geom, 0.1)
       OR ST_DWithin(ST_EndPoint(a.geom), b.geom, 0.1)
)
UPDATE cleaned_roads 
SET is_island = TRUE 
WHERE osm_id NOT IN (SELECT osm_id FROM connected_roads)
AND EXISTS (SELECT 1 FROM cleaned_roads WHERE is_island = FALSE);

DELETE FROM cleaned_roads 
WHERE is_island = TRUE
AND EXISTS (SELECT 1 FROM cleaned_roads WHERE is_island = TRUE);

-- Fix missing street names
UPDATE cleaned_buildings 
SET street_name = nearest.street_name
FROM cleaned_buildings b
CROSS JOIN LATERAL (
	SELECT r.street_name 
    FROM cleaned_roads r 
    WHERE r.street_name IS NOT NULL
    ORDER BY b.geom <-> r.geom
    LIMIT 1
) nearest
WHERE cleaned_buildings.osm_id = b.osm_id
AND cleaned_buildings.street_name IS NULL
AND EXISTS (SELECT 1 FROM cleaned_buildings WHERE street_name IS NULL);


INSERT INTO noded_streets (geom)
SELECT (ST_Dump(ST_Node(ST_Union(ST_SnapToGrid(way, 0.1))))).geom::geometry(LineString, 3857)
FROM planet_osm_line
WHERE highway IN (
    'trunk',
    'trunk_link',
    'primary',
    'primary_link',
    'secondary',
    'secondary_link',
    'tertiary',
    'tertiary_link',
    'unclassified',
    'residential',
    'service',
    'living_street'
)
AND NOT EXISTS (SELECT 1 FROM noded_streets LIMIT 1);


INSERT INTO building_drop_points (building_id, parent_id, geom, building_name)
SELECT 
    b.osm_id,
    NULL::BIGINT,
    ST_ClosestPoint(ST_ExteriorRing(ST_GeometryN(b.geom, 1)), r.geom),
    b.building_name
FROM cleaned_buildings b
CROSS JOIN LATERAL (
    SELECT geom FROM cleaned_roads 
    ORDER BY b.geom <-> geom 
    LIMIT 1
) r
WHERE NOT EXISTS (SELECT 1 FROM building_drop_points LIMIT 1);

-- Metadata
-- Creates the table for the KeyValuePair entity
CREATE TABLE IF NOT EXISTS keyvaluepair (
    id BIGINT NOT NULL,
    key TEXT NOT NULL,
    value TEXT,
    PRIMARY KEY (id, key)
);

-- Add street_name column to network_connections
ALTER TABLE network_connections ADD COLUMN IF NOT EXISTS street_name TEXT;

-- Populate street_name in network_connections by finding the nearest road to each connection's geometry
UPDATE network_points p
SET osm_id = b.building_id
FROM building_drop_points b
WHERE ST_Equals(p.geom, b.geom) AND p.type = 'AGGREGATOR';

UPDATE network_connections
SET street_name = nearest.street_name 
FROM network_connections c
CROSS JOIN LATERAL  (
    SELECT r.street_name 
    FROM cleaned_roads r 
    WHERE r.street_name IS NOT NULL
    ORDER BY c.geom <-> r.geom
    LIMIT 1
) nearest
WHERE network_connections.id = c.id
AND network_connections.street_name IS NULL;
