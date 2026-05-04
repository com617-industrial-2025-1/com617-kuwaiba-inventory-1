package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.rest;

import java.util.List;
import java.util.Optional;

import org.entimoss.kuwaiba.provisioning.model.KuwaibaClass;
import org.entimoss.kuwaiba.provisioning.model.KuwaibaConnection;
import org.entimoss.kuwaiba.provisioning.model.KuwaibaProvisioningRequisition;
import org.entimoss.kuwaiba.provisioning.model.ProjectConstants;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.LinkType;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.LinkedBuilding;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkConnection;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkPoint;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.PointType;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.CleanedBuildingRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.LinkedBuildingRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkConnectionRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkPointRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.PointSerializer;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.LineStringSerializer;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.MultiLineStringSerializer;

@RestController
@RequestMapping("/kuwaiba-network")
public class KuwabaNetworkController {

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
   
   @Autowired
   private LinkedBuildingRepository linkedBuildingRepository;
   
   @Autowired
   private CleanedBuildingRepository cleanedBuildingRepository ;

   // Finding all points with pagenation
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

      return ResponseEntity.ok(createGeometryMapper().writeValueAsString(points));
   }

   // Finding all points by type with pagenation
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
      return ResponseEntity.ok(createGeometryMapper().writeValueAsString(points));
   }

   // Finding all connections with pagenation
   @GetMapping("/connections")
   public ResponseEntity<String> getAllConnections(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) throws Exception {
      String sortBy = "id";
      String sortDirection = "ASC";

      Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
      Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

      Page<NetworkConnection> result = connectionRepository.findAll(pageable);

      List<NetworkConnection> connections = result.getContent();
      return ResponseEntity.ok(createGeometryMapper().writeValueAsString(connections));
   }

   /**
    *  Find all connections by type with pagenation
    * @param linkType
    * @param pageNo
    * @param pageSize
    * @return
    * @throws Exception
    */
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
      return ResponseEntity.ok(createGeometryMapper().writeValueAsString(connections));
   }

   /**
    * This is the main method to create a kuwaiba provisioning requisition for the whole network.
    * @param pageNo
    * @param pageSize
    * @return
    * @throws Exception
    */
   // Finding all connections
   @GetMapping("/kuwaibaRequisition")
   public ResponseEntity<String> getKuwaibaRequisition(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) throws Exception {
      String sortBy = "id";
      String sortDirection = "ASC";

      Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
      Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

      Page<NetworkConnection> result = connectionRepository.findAll(pageable);

      List<NetworkConnection> connections = result.getContent();
      

      KuwaibaProvisioningRequisition pr = new KuwaibaProvisioningRequisition();

      ProjectConstants.addStaticTemplatesToProvisioningRequisition(pr);
      ProjectConstants.addStaticObjectsToProvisioningRequisition(pr);

      // Export buildings
      List<LinkedBuilding> linkedBuildings = linkedBuildingRepository.findAll();
      for (LinkedBuilding lb : linkedBuildings) {
         KuwaibaClass kc = new KuwaibaClass();
         kc.getParentClasses().add(ProjectConstants.parentNeighbourhood);
         kc.setName(lb.getBuildingName());
         kc.setClassName("Building");
         kc.getAttributes().put("uprn", lb.getUprn().toString());
         kc.getAttributes().put("house_num", lb.getHouseNum());
         kc.getAttributes().put("street", lb.getStreetName());
         pr.getKuwaibaClassList().add(kc);
      }

      for (NetworkConnection conn : connections) {

         KuwaibaClass kuwaibaConnectionClass = new KuwaibaClass();
         kuwaibaConnectionClass.setName(conn.getExternal_id());
         kuwaibaConnectionClass.getAttributes().put("link_type", conn.getLink_type().toString());
         kuwaibaConnectionClass.getAttributes().put("geometry", createGeometryMapper().writeValueAsString(conn.getGeom()));
         kuwaibaConnectionClass.getAttributes().put("street_name", conn.getStreetName());

         // Map the KeyValuePair map to the attributes of the KuwaibaClass
         if (conn.getKeyValuePairMap() != null) {
            conn.getKeyValuePairMap().forEach((k, v) -> kuwaibaConnectionClass.getAttributes().put(k, v.getValue()));
         }
         
         switch (conn.getLink_type()) {
         case TRUNK:
            kuwaibaConnectionClass.setClassName("WireContainer");
            kuwaibaConnectionClass.setTemplateName(null);
            break;
         case DISTRIBUTION:
            kuwaibaConnectionClass.setClassName("WireContainer");
            kuwaibaConnectionClass.setTemplateName(null);
            break;
         case FEEDER:
            kuwaibaConnectionClass.setClassName("WireContainer");
            kuwaibaConnectionClass.setTemplateName(null);
            break;
         case DROP:
            kuwaibaConnectionClass.setClassName("WireContainer");
            kuwaibaConnectionClass.setTemplateName(null);
            break;
         }
         
         KuwaibaConnection kuwaibaConnection = new KuwaibaConnection();
         
         kuwaibaConnection.setConnectionClass(kuwaibaConnectionClass);

         // EndpointA - always a network point
         pointRepository.findById(conn.getStart_id()).ifPresent(point -> {
            KuwaibaClass kuwaibaClassStart =pointToKuwaibaClass(point);
            pr.getKuwaibaClassList().add(kuwaibaClassStart);
            kuwaibaConnection.setEndpointA(kuwaibaClassStart);
         });
         
         // When linking buildings, pull UPRN metadata
         // EndpointB - DROP connections reference buildings, others reference network_points
         if (conn.getLink_type() == LinkType.DROP && conn.getOsmId() != null) {
             Optional<LinkedBuilding> linked = linkedBuildingRepository.findById(conn.getOsmId());
             if (linked.isPresent()) {
                 LinkedBuilding lb = linked.get();
                 KuwaibaClass building = new KuwaibaClass();
                 building.setClassName("Building");
                 building.setName(lb.getUprn() != null ? lb.getUprn().toString() : lb.getBuildingName());
                 building.getParentClasses().add(ProjectConstants.parentNeighbourhood);
                 if (lb.getUprn() != null) building.getAttributes().put("uprn", lb.getUprn().toString());
                 if (lb.getStreetName() != null) building.getAttributes().put("street", lb.getStreetName());
                 pr.getKuwaibaClassList().add(building);
                 kuwaibaConnection.setEndpointB(building);
             } else {
                 cleanedBuildingRepository.findById(conn.getOsmId()).ifPresent(cb -> {
                     KuwaibaClass building = new KuwaibaClass();
                     building.setClassName("Building");
                     building.setName(cb.getBuildingName());
                     building.getParentClasses().add(ProjectConstants.parentNeighbourhood);
                     if (cb.getStreetName() != null) building.getAttributes().put("street", cb.getStreetName());
                     pr.getKuwaibaClassList().add(building);
                     kuwaibaConnection.setEndpointB(building);
                 });
             }
         } else {
             pointRepository.findById(conn.getEnd_id()).ifPresent(endPoint -> {
                 KuwaibaClass endPointClass = pointToKuwaibaClass(endPoint);
                 pr.getKuwaibaClassList().add(endPointClass);
                 kuwaibaConnection.setEndpointB(endPointClass);
             });
         }

         pr.getKuwaibaConnectionList().add(kuwaibaConnection);
     }

     return ResponseEntity.ok(createGeometryMapper().writeValueAsString(pr));
 }
   
   public static  KuwaibaClass pointToKuwaibaClass(NetworkPoint point) {
         KuwaibaClass kc = new KuwaibaClass(); 
         kc.getParentClasses().add(ProjectConstants.parentNeighbourhood);
         kc.setName(point.getExternalId());
         
         switch (point.getType()) { 
            case EXCHANGE:
               kc.setClassName("Exchange");
               kc.setTemplateName(null);
               break;
            case CABINET:
               kc.setClassName("Cabinet");
               kc.setTemplateName(null);
               break;
            case POLE:
               kc.setClassName("Pole");
               kc.setTemplateName(null);
               break;
            case AGGREGATOR:
               kc.setClassName("Building");
               kc.setTemplateName(null);
               break;
         }
         
         return kc;
      }

}