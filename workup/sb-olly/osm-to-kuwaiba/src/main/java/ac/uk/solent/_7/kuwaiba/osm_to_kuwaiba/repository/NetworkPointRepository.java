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
                    CAST(CEIL(COUNT(*) OVER() / 8.0) AS INTEGER)
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
                    CAST(CIEL(COUNT(*) OVER() / 8.0) AS INTEGER)   
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
                    CAST(CIEL(COUNT(*) OVER() / 8.0) AS INTEGER)   
                ) OVER() AS cluster_id,
                geom
            FROM network_points
            WHERE type = 'AGGREGATOR'
        ) clustered
        GROUP BY cluster_id
    """)
    void insertExchangeClusters();

    // TODO: Query for finding nearest pole to a given coordinate.
    // Used in RoutingService.poleToBuildingPrediction()

    // TODO: Query for finding all network points near a location.
    // Used for validation and REST controller (maybe)

}
