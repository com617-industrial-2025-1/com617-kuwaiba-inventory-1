package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkPoint;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.PointType;

import java.util.List;

@Repository
public interface NetworkPointRepository extends JpaRepository<NetworkPoint, Long> {

    // These retrievers are to be used in the REST API
    // Find all Points by Type
    // Spring Boot generates from method name
    List<NetworkPoint> findByType(PointType type);
    // Find points by parent_id
    List<NetworkPoint> findByParentId(Long parentId);
    // Find points by parent_id and type
    List<NetworkPoint> findByTypeAndParentId(PointType type, Long parentId);
    // NOTE: Finding all points (findAll()) is inherited from JpaRepository and so doesn't need to be defined here.
    

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO network_points(type, geom, external_id)
        SELECT 
            'CABINET',
            ST_ClosestPoint(r.geom, c.centroid),
            'CABINET-' || gen_random_uuid()
        FROM (
            SELECT ST_Centroid(ST_Collect(geom)) AS centroid
            FROM (
    		    SELECT ST_ClusterKMeans(geom,
                    CAST(CEIL((SELECT COUNT(*) FROM network_points WHERE type = 'POLE') / 8.0) AS INTEGER)
                ) OVER () AS cluster_id,
                geom
            FROM network_points
            WHERE type = 'POLE'
        ) clustered 
        GROUP BY cluster_id
        ) c
        CROSS JOIN LATERAL (
    		SELECT geom FROM cleaned_roads
    		ORDER BY c.centroid <-> geom
    		LIMIT 1
    	) r
    """)
    void insertCabinetClusters();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO network_points(type, geom, external_id)
        SELECT
            'AGGREGATOR',
            ST_ClosestPoint(r.geom, c.centroid),
            'AGGREGATOR-' || gen_random_uuid()
        FROM (
    		SELECT ST_Centroid(ST_Collect(geom)) AS centroid
    		FROM (
    		    SELECT ST_ClusterKMeans(geom,
                    CAST(CEIL((SELECT COUNT(*) FROM network_points WHERE type = 'CABINET') / 8.0) AS INTEGER)   
                ) OVER() AS cluster_id,
                geom
            FROM network_points
            WHERE type = 'CABINET'
        ) clustered
        GROUP BY cluster_id
        ) c
        CROSS JOIN LATERAL (
            SELECT geom FROM cleaned_roads
            ORDER BY c.centroid <-> geom
            LIMIT 1
        ) r
    """)
    void insertAggregatorClusters();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO network_points(type, geom, external_id)
        SELECT
            'EXCHANGE',
            ST_ClosestPoint(r.geom, c.centroid),
            'EXCHANGE-' || gen_random_uuid()
        FROM (
            SELECT ST_Centroid(ST_Collect(geom)) AS centroid
    		FROM (
    		    SELECT ST_ClusterKMeans(geom,
                    CAST(CEIL((SELECT COUNT(*) FROM network_points WHERE type = 'AGGREGATOR') / 8.0) AS INTEGER)   
                ) OVER() AS cluster_id,
                geom
            FROM network_points
            WHERE type = 'AGGREGATOR'
        ) clustered
        GROUP BY cluster_id
        ) c
        CROSS JOIN LATERAL (
            SELECT geom FROM cleaned_roads
            ORDER BY c.centroid <-> geom
            LIMIT 1
        ) r
    """)
    void insertExchangeClusters();

    // Linking children to parents
    // Note that this may change the ratio of children to parents in cases
    // That should be TODO in the next iterative develompment process.

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
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
            WHERE poles.id = nearest.pole_id
    """)
    void updatePoleParents();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
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
            WHERE cabinets.id = nearest.cabinet_id
    """)
    void updateCabinetParents();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
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
            WHERE aggregators.id = nearest.aggregator_id
    """)
    void updateAggregatorParents();
}
