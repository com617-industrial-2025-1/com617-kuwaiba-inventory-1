package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.BuildingDropPoint;

@Repository
public interface BuildingDropPointRepository extends JpaRepository<BuildingDropPoint, Long> {

    // Clusters building drop points into groups of 12. Inserts a pole into the network_points
    // table at the geo center of the cluster.
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO network_points (type, geom, external_id)
        SELECT 
            'POLE',
            ST_ClosestPoint(r.geom, c.centroid),
            'POLE_' || :network_region || '_'  || to_char(c.cluster_id , 'fm00000')
        FROM (
            SELECT ST_Centroid(ST_Collect(geom)) AS centroid, cluster_id
            FROM ( 
    		    SELECT ST_ClusterKMeans(geom,
                    CAST(CEIL((SELECT COUNT(*) FROM building_drop_points) / 12.0) AS INTEGER)
                ) OVER () AS cluster_id,
                geom
            FROM building_drop_points
            ) clustered
        GROUP BY cluster_id
        ) c
        CROSS JOIN LATERAL (
    		SELECT geom from cleaned_roads
    		ORDER BY c.centroid <-> geom
    		LIMIT 1
    	) r
    """)
    void insertPoleClusters(String network_region);


    @Modifying
    @Transactional
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
