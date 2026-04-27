package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.LinkedBuilding;


@Repository
public interface LinkedBuildingRepository extends JpaRepository<LinkedBuilding, Long> {
	List<LinkedBuilding> findByUprn(Long uprn);
	
	@Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO linked_buildings (osm_id, building_name, house_num, street_name, floors, uprn)
        SELECT DISTINCT ON (b.osm_id) 
            b.osm_id, 
            u.uprn::TEXT, -- Building name is now the UPRN (as a String)
            b.house_num, 
            b.street_name, 
            b.floors, 
            u.uprn
        FROM cleaned_buildings b
        JOIN raw_uprns u ON ST_Contains(
            b.geom,
            ST_Transform(ST_SetSRID(ST_Point(u.lon, u.lat), 4326), 3857)
        )
        WHERE NOT EXISTS (SELECT 1 FROM linked_buildings LIMIT 1)
        ORDER BY b.osm_id
        -- DISTINCT ON (b.osm_id) ensures only one UPRN is assigned per building.
    	-- A building polygon may contain more than one UPRN point, which would
    	-- produce duplicate osm_id values and violate the primary key constraint.
    	-- ORDER BY b.osm_id is required by PostgreSQL when using DISTINCT ON.
    """)
    void createLinkedBuildings();
}