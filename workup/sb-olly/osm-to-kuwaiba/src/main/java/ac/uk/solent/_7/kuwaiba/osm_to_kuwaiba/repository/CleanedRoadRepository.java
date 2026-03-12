package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository;

import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models.CleanedRoad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CleanedRoadRepository extends JpaRepository<CleanedRoad, Long> {

}
