package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.rest;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.LinkType;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkConnection;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkPoint;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.PointType;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkConnectionRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkPointRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.PointSerializer;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.LineStringSerializer;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.MultiLineStringSerializer;

@RestController
@RequestMapping("/network")
public class NetworkController {
	
	private ObjectMapper createGeometryMapper() {
	    ObjectMapper mapper = new ObjectMapper();
	    SimpleModule module = new SimpleModule();
	    module.addSerializer(Point.class, new PointSerializer());
	    module.addSerializer(LineString.class, new LineStringSerializer());
	    module.addSerializer(MultiLineString.class, new MultiLineStringSerializer());
	    mapper.registerModule(module);
	    return mapper;
	}
	
	@Autowired
	private NetworkPointRepository pointRepository;
	
	@Autowired
	private NetworkConnectionRepository connectionRepository;
	
	// Finding all points
	@GetMapping("/points")
	public ResponseEntity<String> getAllPoints() throws Exception {
		List<NetworkPoint> points = pointRepository.findAll();
		return ResponseEntity.ok(createGeometryMapper().writeValueAsString(points));
	}
	
	// Finding all points by type
	@GetMapping("/points/type")
    public ResponseEntity<String> getPointsByType(@RequestParam PointType type) throws Exception {
        List<NetworkPoint> points = pointRepository.findByType(type);
        return ResponseEntity.ok(createGeometryMapper().writeValueAsString(points));
    }
	
	// Finding all connections
	@GetMapping("/connections")
	public ResponseEntity<String> getAllConnections() throws Exception {
		List<NetworkConnection> connections = connectionRepository.findAll();
		return ResponseEntity.ok(createGeometryMapper().writeValueAsString(connections));
	}
	
	// Find all connections by type
	@GetMapping("/connections/type")
    public ResponseEntity<String> getConnectionsByType(@RequestParam LinkType linkType) throws Exception {
        List<NetworkConnection> connections = connectionRepository.findByLinkType(linkType);
        return ResponseEntity.ok(createGeometryMapper().writeValueAsString(connections));
    }
	
	@GetMapping("/test/geometry")
	public ResponseEntity<String> testGeometry() throws Exception {	    
	    GeometryFactory gf = new GeometryFactory();
	    Point point = gf.createPoint(new Coordinate(-1.2577, 51.7520));
	    
	    return ResponseEntity.ok(createGeometryMapper().writeValueAsString(point));
	}
}