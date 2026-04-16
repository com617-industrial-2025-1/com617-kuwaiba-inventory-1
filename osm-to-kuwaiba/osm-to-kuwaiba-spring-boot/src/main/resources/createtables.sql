-- schema.sql creates all the tables and then this is run complete data preparation

INSERT INTO cleaned_buildings (osm_id, geom, building_name, house_num, street_name, floors)
SELECT 
    osm_id,
    ST_CollectionExtract(ST_MakeValid(ST_Multi(way)), 3)::geometry(MultiPolygon, 3857),
    COALESCE(tags->'name', 'Building ' || osm_id),
        tags->'addr:housenumber',
        tags->'addr:street',
        tags->'building:levels'
FROM planet_osm_polygon
WHERE building IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM cleaned_buildings LIMIT 1);

INSERT INTO cleaned_roads (osm_id, geom, road_type, street_name)
SELECT 
    osm_id,
    ST_SimplifyPreserveTopology(way, 0.5)::geometry(LineString, 3857),
    highway,
    tags->'name'
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
AND NOT EXISTS (SELECT 1 FROM cleaned_roads LIMIT 1);

-- Remove road islands
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
WHERE osm_id IN (SELECT osm_id FROM islands)
AND EXISTS (SELECT 1 FROM cleaned_roads WHERE is_island = FALSE);

DELETE FROM cleaned_roads 
WHERE is_island = TRUE
AND EXISTS (SELECT 1 FROM cleaned_roads WHERE is_island = TRUE);

-- Fix missing street names
UPDATE cleaned_buildings b
SET street_name = (
    SELECT r.street_name 
    FROM cleaned_roads r 
    WHERE r.street_name IS NOT NULL
    ORDER BY b.geom <-> r.geom
    LIMIT 1
)
WHERE b.street_name IS NULL
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


INSERT INTO building_drop_points (building_id, parent_id, geom)
SELECT 
    b.osm_id,
    NULL::BIGINT,
    ST_ClosestPoint(ST_ExteriorRing(ST_GeometryN(b.geom, 1)), r.geom)
FROM cleaned_buildings b
CROSS JOIN LATERAL (
    SELECT geom FROM cleaned_roads 
    ORDER BY b.geom <-> geom 
    LIMIT 1
) r
WHERE NOT EXISTS (SELECT 1 FROM building_drop_points LIMIT 1);
