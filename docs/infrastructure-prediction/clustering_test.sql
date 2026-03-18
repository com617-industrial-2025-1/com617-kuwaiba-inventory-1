-- ============================================================
-- CLUSTERING TEST SCRIPT
-- Run these queries in order to test the full clustering pipeline.
-- Check network_points and building_drop_points after each step.
-- ============================================================

-- Clear tables for a fresh run
DELETE FROM network_points;
UPDATE building_drop_points SET parent_id = NULL;

-- ============================================================
-- STEP 1a: Predict Poles
-- Clusters building drop points into groups of 12.
-- Inserts one POLE at the centroid of each cluster.
-- Expected result: CEIL(total_buildings / 12) rows in network_points with type = 'POLE'
-- ============================================================
INSERT INTO network_points (type, geom)
SELECT
    'POLE',
    ST_Centroid(ST_Collect(geom))
FROM (
    SELECT
        ST_ClusterKMeans(geom,
            CAST(CEIL((SELECT COUNT(*) FROM building_drop_points) / 12.0) AS INTEGER)
        ) OVER () AS cluster_id,
        geom
    FROM building_drop_points
) clustered
GROUP BY cluster_id;

-- Check poles were created
SELECT COUNT(*) AS pole_count FROM network_points WHERE type = 'POLE';

-- ============================================================
-- STEP 1b: Link Buildings to Poles
-- Updates each building drop point with the id of its nearest pole.
-- Expected result: all rows in building_drop_points have a non-null parent_id
-- ============================================================
UPDATE building_drop_points bdp
SET parent_id = nearest_pole.id
FROM (
    SELECT
        b.building_id,
        p.id
    FROM building_drop_points b
    CROSS JOIN LATERAL (
        SELECT id
        FROM network_points
        WHERE type = 'POLE'
        ORDER BY ST_Distance(geom, b.geom)
        LIMIT 1
    ) p
) nearest_pole
WHERE bdp.building_id = nearest_pole.building_id;

-- Check all buildings have a parent pole
SELECT COUNT(*) AS unlinked_buildings FROM building_drop_points WHERE parent_id IS NULL;

-- ============================================================
-- STEP 2a: Predict Cabinets
-- Clusters poles into groups of 8.
-- Inserts one CABINET at the centroid of each cluster.
-- Expected result: CEIL(pole_count / 8) rows with type = 'CABINET'
-- ============================================================
INSERT INTO network_points (type, geom)
SELECT
    'CABINET',
    ST_Centroid(ST_Collect(geom))
FROM (
    SELECT
        ST_ClusterKMeans(geom,
            CAST(CEIL((SELECT COUNT(*) FROM network_points WHERE type = 'POLE') / 8.0) AS INTEGER)
        ) OVER () AS cluster_id,
        geom
    FROM network_points
    WHERE type = 'POLE'
) clustered
GROUP BY cluster_id;

-- Check cabinets were created
SELECT COUNT(*) AS cabinet_count FROM network_points WHERE type = 'CABINET';

-- ============================================================
-- STEP 2b: Link Poles to Cabinets
-- Updates each pole with the id of its nearest cabinet.
-- Expected result: all POLE rows in network_points have a non-null parent_id
-- ============================================================
UPDATE network_points poles
SET parent_id = nearest.id
FROM (
    SELECT
        p.id AS pole_id,
        c.id
    FROM network_points p
    CROSS JOIN LATERAL (
        SELECT id
        FROM network_points
        WHERE type = 'CABINET'
        ORDER BY ST_Distance(geom, p.geom)
        LIMIT 1
    ) c
    WHERE p.type = 'POLE'
) nearest
WHERE poles.id = nearest.pole_id;

-- Check all poles have a parent cabinet
SELECT COUNT(*) AS unlinked_poles FROM network_points WHERE type = 'POLE' AND parent_id IS NULL;

-- ============================================================
-- STEP 3a: Predict Aggregators
-- Clusters cabinets into groups of 8.
-- Inserts one AGGREGATOR at the centroid of each cluster.
-- Expected result: CEIL(cabinet_count / 8) rows with type = 'AGGREGATOR'
-- ============================================================
INSERT INTO network_points (type, geom)
SELECT
    'AGGREGATOR',
    ST_Centroid(ST_Collect(geom))
FROM (
    SELECT
        ST_ClusterKMeans(geom,
            CAST(CEIL((SELECT COUNT(*) FROM network_points WHERE type = 'CABINET') / 8.0) AS INTEGER)
        ) OVER () AS cluster_id,
        geom
    FROM network_points
    WHERE type = 'CABINET'
) clustered
GROUP BY cluster_id;

-- Check aggregators were created
SELECT COUNT(*) AS aggregator_count FROM network_points WHERE type = 'AGGREGATOR';

-- ============================================================
-- STEP 3b: Link Cabinets to Aggregators
-- Updates each cabinet with the id of its nearest aggregator.
-- Expected result: all CABINET rows in network_points have a non-null parent_id
-- ============================================================
UPDATE network_points cabinets
SET parent_id = nearest.id
FROM (
    SELECT
        c.id AS cabinet_id,
        a.id
    FROM network_points c
    CROSS JOIN LATERAL (
        SELECT id
        FROM network_points
        WHERE type = 'AGGREGATOR'
        ORDER BY ST_Distance(geom, c.geom)
        LIMIT 1
    ) a
    WHERE c.type = 'CABINET'
) nearest
WHERE cabinets.id = nearest.cabinet_id;

-- Check all cabinets have a parent aggregator
SELECT COUNT(*) AS unlinked_cabinets FROM network_points WHERE type = 'CABINET' AND parent_id IS NULL;

-- ============================================================
-- STEP 4a: Predict Exchanges
-- Clusters aggregators into groups of 8.
-- Inserts one EXCHANGE at the centroid of each cluster.
-- Expected result: CEIL(aggregator_count / 8) rows with type = 'EXCHANGE'
-- ============================================================
INSERT INTO network_points (type, geom)
SELECT
    'EXCHANGE',
    ST_Centroid(ST_Collect(geom))
FROM (
    SELECT
        ST_ClusterKMeans(geom,
            CAST(CEIL((SELECT COUNT(*) FROM network_points WHERE type = 'AGGREGATOR') / 8.0) AS INTEGER)
        ) OVER () AS cluster_id,
        geom
    FROM network_points
    WHERE type = 'AGGREGATOR'
) clustered
GROUP BY cluster_id;

-- Check exchanges were created
SELECT COUNT(*) AS exchange_count FROM network_points WHERE type = 'EXCHANGE';

-- ============================================================
-- STEP 4b: Link Aggregators to Exchanges
-- Updates each aggregator with the id of its nearest exchange.
-- Expected result: all AGGREGATOR rows in network_points have a non-null parent_id
-- ============================================================
UPDATE network_points aggregators
SET parent_id = nearest.id
FROM (
    SELECT
        a.id AS aggregator_id,
        e.id
    FROM network_points a
    CROSS JOIN LATERAL (
        SELECT id
        FROM network_points
        WHERE type = 'EXCHANGE'
        ORDER BY ST_Distance(geom, a.geom)
        LIMIT 1
    ) e
    WHERE a.type = 'AGGREGATOR'
) nearest
WHERE aggregators.id = nearest.aggregator_id;

-- Check all aggregators have a parent exchange
SELECT COUNT(*) AS unlinked_aggregators FROM network_points WHERE type = 'AGGREGATOR' AND parent_id IS NULL;

-- ============================================================
-- FINAL CHECK: Summary of all network points created
-- ============================================================
SELECT type, COUNT(*) AS count FROM network_points GROUP BY type ORDER BY type;
