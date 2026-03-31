package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.LinkType;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkConnection;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkPoint;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.PointType;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkConnectionRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkPointRepository;

@RestController
@RequestMapping("/network")
public class NetworkController {
	
	@Autowired
	private NetworkPointRepository pointRepository;
	
	@Autowired
	private NetworkConnectionRepository connectionRepository;
	
	// Finding all points
	@GetMapping("/points")
	public List<NetworkPoint> getAllPoints() {
		return pointRepository.findAll();
	}
	
	// Finding all points by type
	@GetMapping("/points/type")
    public List<NetworkPoint> getPointsByType(@RequestParam PointType type) {
        return pointRepository.findByType(type);
    }
	
	// Finding all connections
	@GetMapping("/connections")
	public List<NetworkConnection> getAllConnections() {
		return connectionRepository.findAll();
	}
	
	// Find all connections by type
	@GetMapping("/connections/type")
    public List<NetworkConnection> getConnectionsByType(@RequestParam LinkType linkType) {
        return connectionRepository.findByLinkType(linkType);
    }
}