package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository;

import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models.NetworkPoint;
import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models.PointType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NetworkPointRepository extends JpaRepository<NetworkPoint, Long> {

    // These retrievers are to be used in the REST API
    // Find all Points by Type
    // Spring Boot generates from method name
    List<NetworkPoint> findByType(PointType type);
    // Find points by parent_id
    List<NetworkPoint> findByParent_id(Long parentId);
    // Find points by parent_id and type
    List<NetworkPoint> findByTypeAndParent_id(PointType type, Long parentId);

    @Modifying
    @Query(nativeQuery = true, value = """
        INSERT INTO network_points(type, geom)
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
        GROUP BY cluster_id
    """)
    void insertCabinetClusters();

    @Modifying
    @Query(nativeQuery = true, value = """
        INSERT INTO network_points(type, geom)
        SELECT
            'AGGREGATOR',
            ST_Centroid(ST_Collect(geom))
        FROM (
            SELECT
                ST_ClusterKMeans(geom,
                    CAST(CEIL((SELECT COUNT(*) FROM network_points WHERE type = 'CABINET') / 8.0) AS INTEGER)   
                ) OVER() AS cluster_id,
                geom
            FROM network_points
            WHERE type = 'CABINET'
        ) clustered
        GROUP BY cluster_id
    """)
    void insertAggregatorClusters();

    @Modifying
    @Query(nativeQuery = true, value = """
        INSERT INTO network_points(type, geom)
        SELECT
            'EXCHANGE',
            ST_Centroid(ST_Collect(geom))
        FROM (
            SELECT
                ST_ClusterKMeans(geom,
                    CAST(CEIL((SELECT COUNT(*) FROM network_points WHERE type = 'AGGREGATOR') / 8.0) AS INTEGER)   
                ) OVER() AS cluster_id,
                geom
            FROM network_points
            WHERE type = 'AGGREGATOR'
        ) clustered
        GROUP BY cluster_id
    """)
    void insertExchangeClusters();

    // Linking children to parents
    // Note that this may change the ratio of children to parents in cases
    // That should be TODO in the next iterative develompment process.

    @Modifying
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
