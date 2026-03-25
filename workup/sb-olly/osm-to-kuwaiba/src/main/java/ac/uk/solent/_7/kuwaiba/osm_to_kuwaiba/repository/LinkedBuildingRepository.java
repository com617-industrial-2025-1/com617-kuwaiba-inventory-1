package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models.LinkedBuilding;


@Repository
public interface LinkedBuildingRepository extends JpaRepository<LinkedBuilding, Long> {
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = """
			
	""")
	void createLinkedBuildings();
}