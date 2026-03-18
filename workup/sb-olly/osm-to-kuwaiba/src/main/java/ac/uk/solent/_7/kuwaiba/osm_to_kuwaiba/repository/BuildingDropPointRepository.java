package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository;

import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models.BuildingDropPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingDropPointRepository extends JpaRepository<BuildingDropPoint, Long> {

    // Clusters building drop points into groups of 12. Inserts a pole into the network_points
    // table at the geo center of the cluster.
    @Modifying
    @Query(nativeQuery = true, value = """
        INSERT INTO network_points (type, geom)
        SELECT 
            'POLE',
            ST_Centroid(ST_Collect(geom))
        FROM (
            SELECT
                ST_ClusterKMeans(geom,
                    CAST(CEIL(COUNT(*) OVER() / 12.0) AS INTEGER)
                ) OVER () AS cluster_id,
                geom
            FROM building_drop_points
            ) clustered
        GROUP BY cluster_id
    """)
    void insertPoleClusters();

    @Modifying
    @Query(nativeQuery = true, value = """
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
            WHERE bdp.building_id = nearest_pole.building_id
    """)
    void updateBuildingParents();
}
