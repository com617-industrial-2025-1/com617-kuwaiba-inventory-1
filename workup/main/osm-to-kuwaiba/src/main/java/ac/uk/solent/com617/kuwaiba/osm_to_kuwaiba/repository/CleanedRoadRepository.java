package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.CleanedRoad;

@Repository
public interface CleanedRoadRepository extends JpaRepository<CleanedRoad, Long> {

}
