package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository;

import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models.NetworkPoint;
import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models.PointType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NetworkPointRepository extends JpaRepository<NetworkPoint, Long> {
    // Find all Points by Type
    // Spring Boot generates from method name
    List<NetworkPoint> findByType(PointType type);
    // Find points by parent_id
    List<NetworkPoint> findByParent_id(Long parentId);
    // Find points by parent_id and type
    List<NetworkPoint> findByTypeAndParent_id(PointType type, Long parentId);

    // TODO: Query for clustering poles

    // TODO: Query for clustering cabinets

    // TODO: Query for clustering aggregators

    // TODO: Query for finding nearest pole to a given coordinate.
    // Used in RoutingService.poleToBuildingPrediction()

    // TODO: Query for finding all network points near a location.
    // Used for validation and REST controller (maybe)

}
