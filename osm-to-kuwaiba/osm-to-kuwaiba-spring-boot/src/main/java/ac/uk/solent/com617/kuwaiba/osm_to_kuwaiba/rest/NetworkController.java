package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.rest;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;


import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.LinkType;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkConnection;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkPoint;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.PointType;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkConnectionRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkPointRepository;
import io.swagger.v3.oas.annotations.Operation;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.PointSerializer;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.ProjectConstants;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.LineStringSerializer;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.MultiLineStringSerializer;

@RestController

@RequestMapping("/network")
public class NetworkController {
   
   private ObjectMapper createGeometryMapper() {
      ObjectMapper mapper = new ObjectMapper();

      mapper.enable(SerializationFeature.INDENT_OUTPUT); // pretty print

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


   @Operation(summary = "Finding all points with pagenation")
   @GetMapping("/points")
   public ResponseEntity<String> getAllPoints(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) throws Exception {

      String sortBy = "id";
      String sortDirection = "ASC";

      Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
      Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

      Page<NetworkPoint> result = pointRepository.findAll(pageable);

      List<NetworkPoint> points = result.getContent();

      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("X-Total-Count", Long.toString(result.getTotalElements()));
      responseHeaders.set("X-Total-Pages", Long.toString(result.getTotalPages()));
      responseHeaders.set("X-Page-Size", Integer.toString(pageSize));
      responseHeaders.set("X-Current-Page", Integer.toString(pageNo));
      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(points), responseHeaders, HttpStatus.OK);

   }

   @Operation(summary = "Finding all points by type with pagenation")
   @GetMapping("/points/type")
   public ResponseEntity<String> getPointsByType(@RequestParam PointType type,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) throws Exception {

      String sortBy = "id";
      String sortDirection = "ASC";

      Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
      Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

      Page<NetworkPoint> result = pointRepository.findByType(type, pageable);
      List<NetworkPoint> points = result.getContent();

      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("X-Total-Count", Long.toString(result.getTotalElements()));
      responseHeaders.set("X-Total-Pages", Long.toString(result.getTotalPages()));
      responseHeaders.set("X-Page-Size", Integer.toString(pageSize));
      responseHeaders.set("X-Current-Page", Integer.toString(pageNo));
      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(points), responseHeaders, HttpStatus.OK);

   }

   /**
    * Find all connections with pagenation, and optional filtering by street name
    * (set findStreetName to UNDEFINED (ProjectConstants.UNDEFINED_STREET) to find connections with no street name, or leave blank/null to find all connections)
    * @param pageNo
    * @param pageSize
    * @param findStreetName
    * @return
    * @throws Exception
    */
   @Operation(summary = "Finding all connections with pagenation, and optional filtering by street name.\n (set findStreetName to UNDEFINED (ProjectConstants.UNDEFINED_STREET) to find connections with no street name, or leave blank/null to find all connections)")
   @GetMapping("/connections")
   public ResponseEntity<String> getAllConnections(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String findStreetName) throws Exception {
      String sortBy = "id";
      String sortDirection = "ASC";

      Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
      Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

      Page<NetworkConnection> result = null;
      if (findStreetName != null) {
         if (findStreetName.equals(ProjectConstants.UNDEFINED_STREET)) {
            result = connectionRepository.findByStreetNameIsNull(pageable);
         } else {
            result = connectionRepository.findByStreetName(findStreetName, pageable);
         }
      } else {
         result = connectionRepository.findAll(pageable);
      }

      List<NetworkConnection> connections = result.getContent();
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("X-Total-Count", Long.toString(result.getTotalElements()));
      responseHeaders.set("X-Total-Pages", Long.toString(result.getTotalPages()));
      responseHeaders.set("X-Page-Size", Integer.toString(pageSize));
      responseHeaders.set("X-Current-Page", Integer.toString(pageNo));
      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(connections),
               responseHeaders, HttpStatus.OK);

   }

   /**
    *  Find all connections by type with pagenation
    * @param linkType
    * @param pageNo
    * @param pageSize
    * @return
    * @throws Exception
    */
   @Operation(summary = "Finding all connections by type with pagenation")
   @GetMapping("/connections/type")
   public ResponseEntity<String> getConnectionsByType(@RequestParam LinkType linkType,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) throws Exception {

      String sortBy = "id";
      String sortDirection = "ASC";

      Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
      Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

      Page<NetworkConnection> result = connectionRepository.findByLinkType(linkType, pageable);
      List<NetworkConnection> connections = result.getContent();

      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("X-Total-Count", Long.toString(result.getTotalElements()));
      responseHeaders.set("X-Total-Pages", Long.toString(result.getTotalPages()));
      responseHeaders.set("X-Page-Size", Integer.toString(pageSize));
      responseHeaders.set("X-Current-Page", Integer.toString(pageNo));
      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(connections),
               responseHeaders, HttpStatus.OK);

   }
   
   @Operation(summary = "Test endpoint to check geometry serialization")
   @GetMapping("/test/geometry")
   public ResponseEntity<String> testGeometry() throws Exception {       
       GeometryFactory gf = new GeometryFactory();
       Point point = gf.createPoint(new Coordinate(-1.2577, 51.7520));
       
       return ResponseEntity.ok(createGeometryMapper().writeValueAsString(point));
   }
	

}