package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.entimoss.kuwaiba.provisioning.model.KuwaibaClass;
import org.entimoss.kuwaiba.provisioning.model.KuwaibaConnection;
import org.entimoss.kuwaiba.provisioning.model.KuwaibaProvisioningRequisition;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.LinkType;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.LinkedBuilding;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkConnection;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkPoint;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.CleanedBuildingRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.LinkedBuildingRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkConnectionRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkPointRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.PointSerializer;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.ProjectConstants;
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
   private CleanedBuildingRepository cleanedBuildingRepository;

   @Autowired
   private ProjectConstants projectConstantValues;


   /**
    * This method sets the project constant values for the network.
    * @param parentContinentName
    * @param parentCountryName
    * @param parentStateName
    * @param parentCityName
    * @param parentLocationName
    * @return JsonObject containing the current project constant values
    * @throws Exception
    */
   @Operation(summary = "Set project constant values for the network. These are used to create the containing parent classes for the kuwaiba provisioning requisition. e.g. Continent, Country, State, City, Location")
   @PostMapping("/projectConstants")
   public ResponseEntity<String> setProjectConstants(
            @RequestParam(defaultValue = ProjectConstants.DEFAULT_PARENT_CONTINENT) String parentContinentName,
            @RequestParam(defaultValue = ProjectConstants.DEFAULT_PARENT_COUNTRY) String parentCountryName,
            @RequestParam(defaultValue = ProjectConstants.DEFAULT_PARENT_STATE) String parentStateName,
            @RequestParam(defaultValue = ProjectConstants.DEFAULT_PARENT_CITY) String parentCityName,
            @RequestParam(defaultValue = ProjectConstants.DEFAULT_PARENT_LOCATION) String parentLocationName) throws Exception {

      if (parentContinentName != null) projectConstantValues.setParentContinentName(parentContinentName);
      if (parentCountryName != null)   projectConstantValues.setParentCountryName(parentCountryName);
      if (parentStateName != null)     projectConstantValues.setParentStateName(parentStateName);
      if (parentCityName != null)      projectConstantValues.setParentCityName(parentCityName);
      if (parentLocationName != null)  projectConstantValues.setParentLocationName(parentLocationName);

      ObjectNode commonValues = createGeometryMapper().createObjectNode();
      commonValues.put("parentContinentName", projectConstantValues.getParentContinentName());
      commonValues.put("parentCountryName", projectConstantValues.getParentCountryName());
      commonValues.put("parentStateName", projectConstantValues.getParentStateName());
      commonValues.put("parentCityName", projectConstantValues.getParentCityName());
      commonValues.put("parentLocationName", projectConstantValues.getParentLocationName());

      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(commonValues),
               HttpStatus.OK);
   }
   
   /**
    * This method gets the current project constant values for the network.
    * @return JsonObject containing the current project constant values
    * @throws Exception
    */
   @Operation(summary = "Get current project constant values for the network.")
   @GetMapping("/projectConstants")
   public ResponseEntity<String> getProjectConstants() throws Exception {

      ObjectNode commonValues = createGeometryMapper().createObjectNode();
      commonValues.put("parentContinentName", projectConstantValues.getParentContinentName());
      commonValues.put("parentCountryName", projectConstantValues.getParentCountryName());
      commonValues.put("parentStateName", projectConstantValues.getParentStateName());
      commonValues.put("parentCityName", projectConstantValues.getParentCityName());
      commonValues.put("parentLocationName", projectConstantValues.getParentLocationName());

      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(commonValues),
               HttpStatus.OK);
   }

   /**
    * This method gets a list of street names in the network.
    * @param pageNo
    * @param pageSize
    * @return
    * @throws Exception
    */
   @Operation(summary = "Get a list of street names in the network.")
   @GetMapping("/streetNames")
   public ResponseEntity<String> getStreetNames() throws Exception {

      HttpHeaders responseHeaders = new HttpHeaders();

      // this could be optimised by creating a custom query to pull distinct street names directly from the database,
      // but this is simpler to implement for now
      Set<String> streetNames = new HashSet<String>();

      List<LinkedBuilding> result = linkedBuildingRepository.findAll();

      for (LinkedBuilding lb : result) {
         if (lb.getStreetName() != null && !lb.getStreetName().isEmpty()) {
            streetNames.add(lb.getStreetName());
         } else {
            streetNames.add(ProjectConstants.UNDEFINED_STREET);
         }
      }

      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(streetNames),
               responseHeaders, HttpStatus.OK);

   }

   /**
    * This is the main method to create a kuwaiba provisioning requisition for the buildings in the network.
    * @param pageNo
    * @param pageSize
    * @param includeStaticTemplates
    * @param includeStaticObjects
    * @param includeBuildings
    * @param includeStreets
    * @param findStreetName   (set UNDEFINED (ProjectConstants.UNDEFINED_STREET) to find buildings with no street name, or leave blank to find all buildings)
    * @return
    * @throws Exception
    */
   @Operation(summary = "Create a kuwaiba provisioning requisition for the buildings in the network. Optional filtering by street name (set findStreetName to UNDEFINED (ProjectConstants.UNDEFINED_STREET) to find buildings with no street name, or leave blank/null to find all buildings)")
   @GetMapping("/kuwaibaRequisitionBuildings")
   public ResponseEntity<String> getKuwaibaRequisitionBuildings(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            
            @Parameter(description = "Includes static kuwaiba template definitions in the requistion. These should be used for the first page but will not be needs for subsequent pages. Default is true.")
            @RequestParam(defaultValue = "true") boolean includeStaticTemplates,
            
            @Parameter(description = "Includes static kuwaiba object definitions in the requistion. These should be used for the first page but will not be needs for subsequent pages. Default is true.")
            @RequestParam(defaultValue = "true") boolean includeStaticObjects,
            
            @Parameter(description = "Include building definitions for either end of the connections. Default is true.")
            @RequestParam(defaultValue = "true") boolean includeBuildings,
            
            @Parameter(description = "Include street definitions for either end of the connections. Default is true.")
            @RequestParam(defaultValue = "true") boolean includeStreets,
            @RequestParam(required = false) String findStreetName) throws Exception {

      KuwaibaProvisioningRequisition pr = new KuwaibaProvisioningRequisition();

      HttpHeaders responseHeaders = new HttpHeaders();

      if (includeStaticTemplates) {
         projectConstantValues.addStaticTemplatesToProvisioningRequisition(pr);
      }

      if (includeStaticObjects) {
         projectConstantValues.addStaticObjectsToProvisioningRequisition(pr);
      }

      LinkedHashMap<String, KuwaibaClass> kuwaibaStreets = new LinkedHashMap<String, KuwaibaClass>();
      List<KuwaibaClass> kuwaibaBuildings = new ArrayList<KuwaibaClass>();

      String sortBy = "osmId";
      String sortDirection = "ASC";

      Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
      Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

      Page<LinkedBuilding> result = null;
      if (findStreetName != null) {
         if (findStreetName.equals(ProjectConstants.UNDEFINED_STREET)) {
            result = linkedBuildingRepository.findByStreetNameIsNull(pageable);
         } else {
            result = linkedBuildingRepository.findByStreetName(findStreetName, pageable);
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
         KuwaibaClass building = new KuwaibaClass();

         if (includeStreets) {
            findStreetName = (lb.getStreetName() != null) ? lb.getStreetName() : ProjectConstants.UNDEFINED_STREET;
            KuwaibaClass streetClass = addKuwaibaStreetClass(findStreetName);
            if (!kuwaibaStreets.containsKey(findStreetName))
               kuwaibaStreets.put(findStreetName, streetClass);
            building.getParentClasses().add(kuwaibaStreets.get(findStreetName));
         } else {
            building.getParentClasses().add(projectConstantValues.getParentNeighbourhoodKuwaibaClass());
         }

         building.setName(lb.getBuildingName());
         building.setClassName("House");
         building.setTemplateName(ProjectConstants.House_01);

         //         kc.getAttributes().put("osmid", lb.getOsmId().toString());
         //         kc.getAttributes().put("uprn", lb.getUprn().toString());
         //         kc.getAttributes().put("house_num", lb.getHouseNum());
         //         kc.getAttributes().put("street", lb.getStreetName());

         // Combine key attributes into a single "address" attribute since not a field in kuwaiba
         building.getAttributes().put("address", "osmid: " + lb.getOsmId() + ", uprn: " + lb.getUprn() + ", " + lb.getHouseNum() + " streetName: " + lb.getStreetName());

         if (lb.getLat() != null)
            building.getAttributes().put("latitude", lb.getLat().toString());
         if (lb.getLon() != null)
            building.getAttributes().put("longitude", lb.getLon().toString());
         kuwaibaBuildings.add(building);
      }

      if (includeStreets)
         pr.getKuwaibaClassList().addAll(kuwaibaStreets.values());
      if (includeBuildings)
         pr.getKuwaibaClassList().addAll(kuwaibaBuildings);

      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(pr),
               responseHeaders, HttpStatus.OK);

   }

   public KuwaibaClass addKuwaibaStreetClass(String streetName) {
      KuwaibaClass street = new KuwaibaClass();
      street.setClassName(ProjectConstants.STREET_CLASS_NAME);
      street.setName(streetName);
      street.getParentClasses().add(projectConstantValues.getParentNeighbourhoodKuwaibaClass());
      return street;
   }

   /**
    * This is the main method to create a kuwaiba provisioning requisition for the connections in the network.
    * @param pageNo
    * @param pageSize
    * @param includeStaticTemplates
    * @param includeStaticObjects
    * @param includeConnectionEndPoints
    * @param includeConnections
    * @param includeStreets
    * @param findStreetName (set UNDEFINED (ProjectConstants.UNDEFINED_STREET) to find connections with no street name, or leave blank/null to find all connections)
    * @return
    * @throws Exception
    */
   @Operation( summary = "Create a kuwaiba provisioning requisition for the connections in the network. Optional filtering by street name (set findStreetName to UNDEFINED (ProjectConstants.UNDEFINED_STREET) to find connections with no street name, or leave blank/null to find all connections)")
   @GetMapping("/kuwaibaRequisitionConnections")
   public ResponseEntity<String> getKuwaibaRequisition(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "true") boolean includeStaticTemplates,
            @RequestParam(defaultValue = "true") boolean includeStaticObjects,
            @RequestParam(defaultValue = "true") boolean includeConnectionEndPoints,
            @RequestParam(defaultValue = "true") boolean includeConnections,
            @RequestParam(defaultValue = "true") boolean includeStreets,
            @RequestParam(required = false) String findStreetName) throws Exception {

      Map<String, KuwaibaClass> kuwaibaStreets = new LinkedHashMap<String, KuwaibaClass>();
      Map<String, KuwaibaConnection> kuwaibaConnections = new LinkedHashMap<String, KuwaibaConnection>();
      Map<String, KuwaibaClass> kuwaibaConnectionEndPoints = new LinkedHashMap<String, KuwaibaClass>();

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

      KuwaibaProvisioningRequisition pr = new KuwaibaProvisioningRequisition();

      if (includeStaticTemplates) {
         projectConstantValues.addStaticTemplatesToProvisioningRequisition(pr);
      }

      if (includeStaticObjects) {
         projectConstantValues.addStaticObjectsToProvisioningRequisition(pr);
      }

      for (NetworkConnection conn : connections) {

         KuwaibaClass kuwaibaConnectionClass = new KuwaibaClass();

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
            kuwaibaConnectionClass.setTemplateName(ProjectConstants.BFU_4_12);
            break;
         case DISTRIBUTION:
            kuwaibaConnectionClass.setClassName("WireContainer");
            kuwaibaConnectionClass.setTemplateName(ProjectConstants.BFU_4_12);
            break;
         case FEEDER:
            kuwaibaConnectionClass.setClassName("WireContainer");
            kuwaibaConnectionClass.setTemplateName(ProjectConstants.BFU_4_12);
            break;
         case DROP:
            kuwaibaConnectionClass.setClassName("WireContainer");
            kuwaibaConnectionClass.setTemplateName(ProjectConstants.BFU_1_2);
            break;
         default:
            kuwaibaConnectionClass.setClassName("WireContainer");
            kuwaibaConnectionClass.setTemplateName(ProjectConstants.STREET_DUCT_1_EMPTY);
            break;
         }

         KuwaibaConnection kuwaibaConnection = new KuwaibaConnection();

         kuwaibaConnection.setConnectionClass(kuwaibaConnectionClass);

         // EndpointA - always a network point
         pointRepository.findById(conn.getStart_id()).ifPresent(point -> {

            KuwaibaClass endPointClass = pointToKuwaibaClass(point);

            if (includeStreets) {
               String streetName = (point.getRelatedRoadName() != null) ? point.getRelatedRoadName() : ProjectConstants.UNDEFINED_STREET;
               KuwaibaClass streetClass = addKuwaibaStreetClass(streetName);
               if (!kuwaibaStreets.containsKey(streetName))
                  kuwaibaStreets.put(streetName, streetClass);
               endPointClass.getParentClasses().add(kuwaibaStreets.get(streetName));
            } else {
               endPointClass.getParentClasses().add(projectConstantValues.getParentNeighbourhoodKuwaibaClass());
            }

            if (!kuwaibaConnectionEndPoints.containsKey(endPointClass.getName()))
               kuwaibaConnectionEndPoints.put(endPointClass.getName(), endPointClass);

            kuwaibaConnection.setEndpointA(endPointClass);
         });

         // EndpointB - could be a building or a network point
         // When linking buildings, pull UPRN metadata

         // EndpointB - DROP connections reference buildings, others reference network_points
         if (conn.getLink_type() == LinkType.DROP && conn.getOsmId() != null) {

            // end point is a house building
            Optional<LinkedBuilding> linked = linkedBuildingRepository.findById(conn.getOsmId());
            if (linked.isPresent()) {
               LinkedBuilding lb = linked.get();

               KuwaibaClass building = new KuwaibaClass();

               if (includeStreets) {
                  String streetName = (lb.getStreetName() != null) ? lb.getStreetName() : ProjectConstants.UNDEFINED_STREET;
                  KuwaibaClass streetClass = addKuwaibaStreetClass(streetName);
                  if (!kuwaibaStreets.containsKey(streetName))
                     kuwaibaStreets.put(streetName, streetClass);
                  building.getParentClasses().add(kuwaibaStreets.get(streetName));
               } else {
                  building.getParentClasses().add(projectConstantValues.getParentNeighbourhoodKuwaibaClass());
               }

               building.setName(lb.getBuildingName());
               building.setClassName("House");
               building.setTemplateName(ProjectConstants.House_01);

               //               KuwaibaClass building = new KuwaibaClass();
               //               building.setClassName("Building");
               //               building.setName(lb.getUprn() != null ? lb.getUprn().toString() : lb.getBuildingName());
               //               building.getParentClasses().add(ProjectConstants.parentNeighbourhood);
               //               if (lb.getOsmId() != null)building.getAttributes().put("osmid", lb.getOsmId().toString());
               //               if (lb.getUprn() != null) building.getAttributes().put("uprn", lb.getUprn().toString());
               //               if (lb.getStreetName() != null) building.getAttributes().put("street", lb.getStreetName());

               // Combine key attributes into a single "address" attribute since not a field in kuwaiba
               building.getAttributes().put("address", "osmid: " + lb.getOsmId() + ", uprn: " + lb.getUprn() + ", " + lb.getHouseNum() + " streetName: " + lb.getStreetName());

               if (lb.getLat() != null)
                  building.getAttributes().put("latitude", lb.getLat().toString());
               if (lb.getLon() != null)
                  building.getAttributes().put("longitude", lb.getLon().toString());

               if (!kuwaibaConnectionEndPoints.containsKey(building.getName()))
                  kuwaibaConnectionEndPoints.put(building.getName(), building);

               kuwaibaConnection.setEndpointB(building);
            } else {
               cleanedBuildingRepository.findById(conn.getOsmId()).ifPresent(cb -> {

                  KuwaibaClass building = new KuwaibaClass();

                  if (includeStreets) {
                     String streetName = (cb.getStreetName() != null) ? cb.getStreetName() : ProjectConstants.UNDEFINED_STREET;
                     KuwaibaClass streetClass = addKuwaibaStreetClass(streetName);
                     if (!kuwaibaStreets.containsKey(streetName))
                        kuwaibaStreets.put(streetName, streetClass);
                     building.getParentClasses().add(kuwaibaStreets.get(streetName));
                  } else {
                     building.getParentClasses().add(projectConstantValues.getParentNeighbourhoodKuwaibaClass());
                  }

                  building.setName(cb.getBuildingName());
                  building.setClassName("House");
                  building.setTemplateName(ProjectConstants.House_01);

                  // building.setClassName("Building");
                  // building.setName(cb.getBuildingName());
                  // building.getParentClasses().add(ProjectConstants.parentNeighbourhood);
                  //if (cb.getStreetName() != null) building.getAttributes().put("street", cb.getStreetName());

                  // Combine key attributes into a single "address" attribute since not a field in kuwaiba
                  building.getAttributes().put("address", "streetName: " + cb.getStreetName());

                  if (!kuwaibaConnectionEndPoints.containsKey(building.getName()))
                     kuwaibaConnectionEndPoints.put(building.getName(), building);

                  kuwaibaConnection.setEndpointB(building);
               });
            }
         } else {
            //end point is a network point
            pointRepository.findById(conn.getEnd_id()).ifPresent(endPoint -> {

               KuwaibaClass endPointClass = pointToKuwaibaClass(endPoint);

               if (includeStreets) {
                  String streetName = (endPoint.getRelatedRoadName() != null) ? endPoint.getRelatedRoadName() : ProjectConstants.UNDEFINED_STREET;
                  KuwaibaClass streetClass = addKuwaibaStreetClass(streetName);
                  if (!kuwaibaStreets.containsKey(streetName))
                     kuwaibaStreets.put(streetName, streetClass);
                  endPointClass.getParentClasses().add(kuwaibaStreets.get(streetName));
               } else {
                  endPointClass.getParentClasses().add(projectConstantValues.getParentNeighbourhoodKuwaibaClass());
               }

               if (!kuwaibaConnectionEndPoints.containsKey(endPointClass.getName()))
                  kuwaibaConnectionEndPoints.put(endPointClass.getName(), endPointClass);

               kuwaibaConnection.setEndpointB(endPointClass);
            });
         }

         //kuwaibaConnectionClass.setName(conn.getExternal_id()+"_"+kuwaibaConnectionClass.getTemplateName()); // data base names incorrect

         String connectionBaseName = conn.getLink_type() + "_" + kuwaibaConnection.getEndpointA().getName() + "_" + kuwaibaConnection.getEndpointB().getName();
         String connectionClassName = connectionBaseName + "_" + kuwaibaConnectionClass.getTemplateName();

         kuwaibaConnectionClass.setName(connectionClassName);

         kuwaibaConnections.put(kuwaibaConnection.getConnectionClass().getName(), kuwaibaConnection);

      }

      // Add the streets to the provisioning requisition
      if (includeStreets)
         pr.getKuwaibaClassList().addAll(kuwaibaStreets.values());

      // Add the endpoints to the provisioning requisition
      if (includeConnectionEndPoints)
         pr.getKuwaibaClassList().addAll(kuwaibaConnectionEndPoints.values());

      // Add the connections to the provisioning requisition
      if (includeConnections)
         pr.getKuwaibaConnectionList().addAll(kuwaibaConnections.values());

      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("X-Total-Count", Long.toString(result.getTotalElements()));
      responseHeaders.set("X-Total-Pages", Long.toString(result.getTotalPages()));
      responseHeaders.set("X-Page-Size", Integer.toString(pageSize));
      responseHeaders.set("X-Current-Page", Integer.toString(pageNo));
      return new ResponseEntity<String>(createGeometryMapper().writeValueAsString(pr),
               responseHeaders, HttpStatus.OK);

   }

   public KuwaibaClass pointToKuwaibaClass(NetworkPoint point) {
      KuwaibaClass kc = new KuwaibaClass();

      kc.setName(point.getExternalId());

      try {

         double[] latLon = CoordinateTranslator.metersToLatLon(point.getGeom().getX(), point.getGeom().getY());

         kc.getAttributes().put("latitude", Double.toString(latLon[0]));
         kc.getAttributes().put("longitude", Double.toString(latLon[1]));
      } catch (Exception e) {
         System.out.println("Error converting geometry to attributes for point " + point.getId() + ": " + e.getMessage());
      }

      switch (point.getType()) {
      case EXCHANGE:
         kc.setClassName("Facility");
         kc.setTemplateName(ProjectConstants.FEX_FACILITY_001);
         break;
      case AGGREGATOR:
         kc.setClassName("OutdoorsCabinet");
         kc.setTemplateName(ProjectConstants.CAB_10SPL8);
         break;
      case CABINET:
         kc.setClassName("OutdoorsCabinet");
         kc.setTemplateName(ProjectConstants.CAB_10SPL16);
         break;
      case POLE:
         kc.setClassName("Pole");
         kc.setTemplateName(ProjectConstants.POLE_2_16drop);
         break;
      case BUILDING:
         kc.setClassName("House");
         kc.setTemplateName(ProjectConstants.House_01);
         break;
      default:
         kc.setClassName("UNKNOWN_POINT_TYPE");
         kc.setTemplateName(null);
         break;
      }

      return kc;
   }

}