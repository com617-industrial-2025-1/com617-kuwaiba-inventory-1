package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.entimoss.kuwaiba.provisioning.model.KuwaibaClass;
import org.entimoss.kuwaiba.provisioning.model.KuwaibaConnection;
import org.entimoss.kuwaiba.provisioning.model.KuwaibaProvisioningRequisition;
import org.entimoss.kuwaiba.provisioning.model.ProjectConstants;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.CoordinateTranslator;
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

      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("X-Total-Count", Long.toString(result.getTotalElements()));
      responseHeaders.set("X-Total-Pages", Long.toString(result.getTotalPages()));
      responseHeaders.set("X-Page-Size", Integer.toString(pageSize));
      responseHeaders.set("X-Current-Page", Integer.toString(pageNo));
      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(points), responseHeaders, HttpStatus.OK);

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

      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("X-Total-Count", Long.toString(result.getTotalElements()));
      responseHeaders.set("X-Total-Pages", Long.toString(result.getTotalPages()));
      responseHeaders.set("X-Page-Size", Integer.toString(pageSize));
      responseHeaders.set("X-Current-Page", Integer.toString(pageNo));
      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(points), responseHeaders, HttpStatus.OK);

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

   /**
    * This is the main method to create a kuwaiba provisioning requisition for the buildings in the network.
    * @param pageNo
    * @param pageSize
    * @return
    * @throws Exception
    */
   @GetMapping("/streetNames")
   public ResponseEntity<String> getStreetNames(     
            ) throws Exception {


      HttpHeaders responseHeaders = new HttpHeaders();

      // this could be optimised by creating a custom query to pull distinct street names directly from the database,
      // but this is simpler to implement for now
      Set<String> streetNames = new HashSet<String>();

      List<LinkedBuilding> result = linkedBuildingRepository.findAll();

      for (LinkedBuilding lb : result) {
         if (lb.getStreetName() != null && !lb.getStreetName().isEmpty()) { 
            streetNames.add(lb.getStreetName());
         }  else {
            streetNames.add("UNDEFINED");
         }
      }

      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(streetNames ),
               responseHeaders, HttpStatus.OK);

   }

   /**
    * This is the main method to create a kuwaiba provisioning requisition for the buildings in the network.
    * @param pageNo
    * @param pageSize
    * @return
    * @throws Exception
    */
   @GetMapping("/kuwaibaRequisitionBuildings")
   public ResponseEntity<String> getKuwaibaRequisitionBuildings(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "true") boolean includeStaticTemplates,
            @RequestParam(defaultValue = "true") boolean includeStaticObjects,
            @RequestParam(defaultValue = "true") boolean includeBuildings,
            @RequestParam(defaultValue = "true") boolean includeStreets,
            @RequestParam(required = false) String streetName
            ) throws Exception {


      KuwaibaProvisioningRequisition pr = new KuwaibaProvisioningRequisition();

      HttpHeaders responseHeaders = new HttpHeaders();

      if (includeStaticTemplates) {
         ProjectConstants.addStaticTemplatesToProvisioningRequisition(pr);
      }

      if (includeStaticObjects) {
         ProjectConstants.addStaticObjectsToProvisioningRequisition(pr);
      }




      LinkedHashMap<String, KuwaibaClass> kuwaibaStreets = new LinkedHashMap<String,KuwaibaClass>();
      List<KuwaibaClass> kuwaibaBuildings = new ArrayList<KuwaibaClass>();

      String sortBy = "osmId";
      String sortDirection = "ASC";

      Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
      Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

      Page<LinkedBuilding> result = null;
      if(streetName !=null ) {
         if (streetName.equals("UNDEFINED")) {
            result = linkedBuildingRepository.findByStreetNameIsNull(pageable);
         } else {
            result = linkedBuildingRepository.findByStreetName(streetName, pageable);
         }
      } else {
         result = linkedBuildingRepository.findAll(pageable);
      }

      List<LinkedBuilding> linkedBuildings = result.getContent();

      responseHeaders.set("X-Total-Count", Long.toString(result.getTotalElements()));
      responseHeaders.set("X-Total-Pages", Long.toString(result.getTotalPages()));
      responseHeaders.set("X-Page-Size", Integer.toString(pageSize));
      responseHeaders.set("X-Current-Page", Integer.toString(pageNo));

      for (LinkedBuilding lb : linkedBuildings) {
         KuwaibaClass kc = new KuwaibaClass();

         if (includeStreets) {
            streetName = (lb.getStreetName() != null) ? lb.getStreetName() : "UNDEFINED";
            KuwaibaClass streetClass = addKuwaibaStreetClass(streetName);
            if (! kuwaibaStreets.containsKey(streetName)) kuwaibaStreets.put(streetName,streetClass);
            kc.getParentClasses().add(kuwaibaStreets.get(streetName));
         } else {
            kc.getParentClasses().add(ProjectConstants.parentNeighbourhood);
         }

         kc.setName(lb.getBuildingName());
         kc.setClassName("Building");
         kc.getAttributes().put("osmid", lb.getOsmId().toString());
         kc.getAttributes().put("uprn", lb.getUprn().toString());
         kc.getAttributes().put("house_num", lb.getHouseNum());
         kc.getAttributes().put("street", lb.getStreetName());
         if (lb.getLat()!=null) kc.getAttributes().put("latitude",  lb.getLat().toString());
         if (lb.getLon()!=null) kc.getAttributes().put("longitude",  lb.getLon().toString());
         kuwaibaBuildings.add(kc);
      }

      if (includeStreets) pr.getKuwaibaClassList().addAll(kuwaibaStreets.values());
      if (includeBuildings) pr.getKuwaibaClassList().addAll(kuwaibaBuildings);

      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(pr),
               responseHeaders, HttpStatus.OK);

   }

   static KuwaibaClass addKuwaibaStreetClass(String streetName) {
      KuwaibaClass street = new KuwaibaClass();
      street.setClassName(ProjectConstants.STREET_CLASS_NAME);
      street.setName(streetName);
      street.getParentClasses().add(ProjectConstants.parentNeighbourhood);
      return street;
   }

   /**
    * This is the main method to create a kuwaiba provisioning requisition for the whole network.
    * @param pageNo
    * @param pageSize
    * @return
    * @throws Exception
    */
   // Finding all connections
   @GetMapping("/kuwaibaRequisitionConnections")
   public ResponseEntity<String> getKuwaibaRequisition(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "true") boolean includeStaticTemplates,
            @RequestParam(defaultValue = "true") boolean includeStaticObjects,
            @RequestParam(defaultValue = "true") boolean includeConnectionBuildings,
            @RequestParam(defaultValue = "true") boolean includeConnections            
            ) throws Exception {
      String sortBy = "id";
      String sortDirection = "ASC";

      Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
      Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

      Page<NetworkConnection> result = connectionRepository.findAll(pageable);

      List<NetworkConnection> connections = result.getContent();

      KuwaibaProvisioningRequisition pr = new KuwaibaProvisioningRequisition();

      if (includeStaticTemplates) {
         ProjectConstants.addStaticTemplatesToProvisioningRequisition(pr);
      }

      if (includeStaticObjects) {
         ProjectConstants.addStaticObjectsToProvisioningRequisition(pr);
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

            if (includeConnectionBuildings) pr.getKuwaibaClassList().add(kuwaibaClassStart);

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
               if (lb.getOsmId() != null)building.getAttributes().put("osmid", lb.getOsmId().toString());
               if (lb.getUprn() != null) building.getAttributes().put("uprn", lb.getUprn().toString());
               if (lb.getStreetName() != null) building.getAttributes().put("street", lb.getStreetName());
               if (lb.getLat()!=null) building.getAttributes().put("latitude",  lb.getLat().toString());
               if (lb.getLon()!=null) building.getAttributes().put("longitude",  lb.getLon().toString());

               if (includeConnectionBuildings)  pr.getKuwaibaClassList().add(building);

               kuwaibaConnection.setEndpointB(building);
            } else {
               cleanedBuildingRepository.findById(conn.getOsmId()).ifPresent(cb -> {
                  KuwaibaClass building = new KuwaibaClass();
                  building.setClassName("Building");
                  building.setName(cb.getBuildingName());
                  building.getParentClasses().add(ProjectConstants.parentNeighbourhood);
                  if (cb.getStreetName() != null) building.getAttributes().put("street", cb.getStreetName());

                  if (includeConnectionBuildings) pr.getKuwaibaClassList().add(building);

                  kuwaibaConnection.setEndpointB(building);
               });
            }
         } else {
            pointRepository.findById(conn.getEnd_id()).ifPresent(endPoint -> {
               KuwaibaClass endPointClass = pointToKuwaibaClass(endPoint);

               if (includeConnectionBuildings)  pr.getKuwaibaClassList().add(endPointClass);

               kuwaibaConnection.setEndpointB(endPointClass);
            });
         }

         // Add the connection to the provisioning requisition
         if (includeConnections) pr.getKuwaibaConnectionList().add(kuwaibaConnection);

      }


      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("X-Total-Count", Long.toString(result.getTotalElements()));
      responseHeaders.set("X-Total-Pages", Long.toString(result.getTotalPages()));
      responseHeaders.set("X-Page-Size", Integer.toString(pageSize));
      responseHeaders.set("X-Current-Page", Integer.toString(pageNo));
      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(pr),
               responseHeaders, HttpStatus.OK);

   }


   public static  KuwaibaClass pointToKuwaibaClass(NetworkPoint point) {
      KuwaibaClass kc = new KuwaibaClass(); 
      kc.getParentClasses().add(ProjectConstants.parentNeighbourhood);
      kc.setName(point.getExternalId());

      try {

         double[] latLon = CoordinateTranslator.metersToLatLon(point.getGeom().getX(), point.getGeom().getY());

         kc.getAttributes().put("latitude",  Double.toString(latLon[0]));
         kc.getAttributes().put("longitude",  Double.toString(latLon[1]));
      } catch (Exception e) {
         System.out.println("Error converting geometry to attributes for point " + point.getId() + ": " + e.getMessage());
      }

      switch (point.getType()) { 
      case EXCHANGE:
         kc.setClassName("Facility");
         kc.setTemplateName(null);
         break;
      case CABINET:
         kc.setClassName("OutdoorsCabinet");
         kc.setTemplateName(null);
         break;
      case POLE:
         kc.setClassName("Pole");
         kc.setTemplateName(null);
         break;
      case AGGREGATOR:
         kc.setClassName("OutdoorsCabinet");
         kc.setTemplateName(null);
         break;
      case BUILDING:
         kc.setClassName("Building");
         kc.setTemplateName(null);
         break;
      default:
         kc.setClassName("UNKNOWN_POINT_TYPE");
         kc.setTemplateName(null);
         break;
      }

      return kc;
   }

}