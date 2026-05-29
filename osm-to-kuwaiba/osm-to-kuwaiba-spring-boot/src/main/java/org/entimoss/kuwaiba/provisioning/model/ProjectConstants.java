package org.entimoss.kuwaiba.provisioning.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.entimoss.kuwaiba.provisioning.model.KuwaibaClass;
import org.entimoss.kuwaiba.provisioning.model.KuwaibaTemplateDefinition;


public class ProjectConstants {
   
   public static final String PARENT_CONTINENT = "Europe";
   public static final String PARENT_COUNTRY = "Great Britain";
   public static final String PARENT_STATE = "Hampshire";
   public static final String PARENT_CITY = "Southampton";
   public static final String PARENT_FACILITY = "SOTNOO1"; // fex

   public static final String PARENT_LOCATION_CLASS_NAME = "Neighborhood";
   public static final String PARENT_LOCATION_VALUE = "BitternePk";
   
   public static final String STREET_CLASS_NAME = "Neighborhood";
   public static final String UNDEFINED_STREET = "UNDEFINED";
   
   public static  KuwaibaClass parentNeighbourhood =null;
   
   public static KuwaibaProvisioningRequisition  addStaticObjectsToProvisioningRequisition(KuwaibaProvisioningRequisition pr) {

      // create Southampton if doesn't exist
      // block to isolate repeat variables
      try {
         KuwaibaClass kuwaibaClass1 = new KuwaibaClass();
         pr.getKuwaibaClassList().add(kuwaibaClass1);

         kuwaibaClass1.setName(PARENT_CITY); // southampton
         kuwaibaClass1.setClassName("City");

         KuwaibaClass parent1 = new KuwaibaClass();
         kuwaibaClass1.getParentClasses().add(parent1);
         parent1.setName(PARENT_STATE); //hampshire
         parent1.setClassName("State");

      } catch (Exception e) {
         e.printStackTrace();
      }

      // create bitterne park neighbourhood  if doesn't exist
      // block to isolate repeat variables
      try {
         KuwaibaClass kuwaibaClass1 = new KuwaibaClass();
         pr.getKuwaibaClassList().add(kuwaibaClass1);

         kuwaibaClass1.setClassName(PARENT_LOCATION_CLASS_NAME); // Neighborhood
         kuwaibaClass1.setName(PARENT_LOCATION_VALUE); // bitterne pk
         
         parentNeighbourhood = kuwaibaClass1;

         KuwaibaClass parent1 = new KuwaibaClass();
         kuwaibaClass1.getParentClasses().add(parent1);
         parent1.setName(PARENT_CITY); //hampshire
         parent1.setClassName("City");

      } catch (Exception e) {
         e.printStackTrace();
      }
           
      return pr;

   }
   
   public static KuwaibaProvisioningRequisition addStaticTemplatesToProvisioningRequisition(KuwaibaProvisioningRequisition pr) {

      List<KuwaibaTemplateDefinition> kuwaibaTemplateDefinitionList = new ArrayList<KuwaibaTemplateDefinition>();

      // ONT_CONTAINER_CLASS_NAME = "House";
      // ONT_CONTAINER_TEMPLATE_NAME = "House_01";
      //         
      // ONT_CLASS_NAME = "OpticalNetworkTerminal";
      // ONT_TEMPLATE_NAME = "ONT_NOKIA_01";
      // ONC_CLASS_NAME = "SpliceBox";
      // CSP_TEMPLATE_NAME = "CSP_BFU_1_2_01";

      // block to isolate local variables  
      // creating template from function definitions
      try {
         KuwaibaTemplateDefinition definition1 = new KuwaibaTemplateDefinition();
         definition1.setTemplateName("House_01");
         definition1.setTemplateElementName("House_01");
         definition1.setClassName("House");
         definition1.setSpecial(false);

//         // ONT
//         KuwaibaTemplateDefinition childDefinition1 = new KuwaibaTemplateDefinition();
//         childDefinition1.setTemplateElementName("ONT_NOKIA_01");
//         childDefinition1.setClassName("OpticalNetworkTerminal");
//         childDefinition1.setSpecial(false);
//         definition1.getChildKuwaibaTemplateDefinitions().add(childDefinition1);
//
//         KuwaibaTemplateDefinition childDefinition1_1 = new KuwaibaTemplateDefinition();
//         childDefinition1_1.setTemplateElementName("IN-01");
//         childDefinition1_1.setClassName("OpticalPort");
//         childDefinition1_1.setSpecial(false);
//         childDefinition1.getChildKuwaibaTemplateDefinitions().add(childDefinition1_1);
//
//         KuwaibaTemplateDefinition childDefinition1_2 = new KuwaibaTemplateDefinition();
//         childDefinition1_2.setTemplateElementName("eth0");
//         childDefinition1_2.setClassName("ElectricalPort");
//         childDefinition1_2.setSpecial(false);
//         childDefinition1.getChildKuwaibaTemplateDefinitions().add(childDefinition1_2);
//
//         // CSP
//         KuwaibaTemplateDefinition childDefinition2 = new KuwaibaTemplateDefinition();
//         childDefinition2.setTemplateElementName("CSP_BFU_1_2_01");
//         childDefinition2.setClassName("SpliceBox");
//         childDefinition2.setSpecial(false);
//         // build ports using function
//         childDefinition2.setTemplateFunction("OpticalSpliceBoxFunction");
//         HashMap<String, String> attributes1 = new HashMap<String, String>();
//         attributes1.put("numberOfPorts", "2");
//         childDefinition2.setTemplateFunctionAttributes(attributes1);
//
//         definition1.getChildKuwaibaTemplateDefinitions().add(childDefinition2);

         kuwaibaTemplateDefinitionList.add(definition1);

      } catch (Exception e) {
         throw new IllegalArgumentException("problem creating definition");
      }

      //  SECONDARY_SPLITTER_CONTAINER_CLASS_NAME = "Pole";
      //  SECONDARY_SPLITTER_CONTAINER_TEMPLATE_NAME = "POLE_2_16drop";
      //  SECONDARY_SPLITTER_CLASS_NAME = "FiberSplitter";
      //  SECONDARY_SPLITTER_TEMPLATE_NAME = "SPL16";

      // block to isolate local variables  
      // creating template from function definitions
      try {
         KuwaibaTemplateDefinition definition1 = new KuwaibaTemplateDefinition();
         definition1.setTemplateName("POLE_2_16drop");
         definition1.setTemplateElementName("POLE_2_16drop");
         definition1.setClassName("Pole");
         definition1.setSpecial(false);

//         // 2 x 8 way splitters in template
//         for (int splitterNo = 1; splitterNo <= 2; splitterNo++) {
//            KuwaibaTemplateDefinition childDefinition1 = new KuwaibaTemplateDefinition();
//            childDefinition1.setTemplateElementName("SPL16_" + String.format("%03d", splitterNo));
//            childDefinition1.setClassName("FiberSplitter");
//            childDefinition1.setSpecial(false);
//            // build ports using function
//            childDefinition1.setTemplateFunction("FiberSplitterFunction");
//            HashMap<String, String> attributes1 = new HashMap<String, String>();
//            attributes1.put("numberOfPorts", "16");
//            childDefinition1.setTemplateFunctionAttributes(attributes1);
//
//            definition1.getChildKuwaibaTemplateDefinitions().add(childDefinition1);
//         }

         kuwaibaTemplateDefinitionList.add(definition1);

      } catch (Exception e) {
         throw new IllegalArgumentException("problem creating definition");
      }

      // PRIMARY_SPLITTER_CONTAINER_TEMPLATE_NAME = "CAB_10SPL8";
      // PRIMARY_SPLITTER_CONTAINER_CLASS_NAME = "OutdoorsCabinet";
      //         
      // PRIMARY_SPLITTER_CLASS_NAME = "FiberSplitter";
      // PRIMARY_SPLITTER_TEMPLATE_NAME = "SPL8";

      // block to isolate local variables  
      // creating template from function definitions
      try {
         KuwaibaTemplateDefinition definition1 = new KuwaibaTemplateDefinition();
         definition1.setTemplateName("CAB_10SPL8");
         definition1.setTemplateElementName("CAB_10SPL8");
         definition1.setClassName("OutdoorsCabinet");
         definition1.setSpecial(false);

//         // 10 splitters in template
//         for (int splitterNo = 1; splitterNo <= 10; splitterNo++) {
//            KuwaibaTemplateDefinition childDefinition1 = new KuwaibaTemplateDefinition();
//            childDefinition1.setTemplateElementName("SPL8_" + String.format("%03d", splitterNo));
//            childDefinition1.setClassName("FiberSplitter");
//            childDefinition1.setSpecial(false);
//            // build ports using function
//            childDefinition1.setTemplateFunction("FiberSplitterFunction");
//            HashMap<String, String> attributes1 = new HashMap<String, String>();
//            attributes1.put("numberOfPorts", "8");
//            childDefinition1.setTemplateFunctionAttributes(attributes1);
//
//            definition1.getChildKuwaibaTemplateDefinitions().add(childDefinition1);
//         }

         kuwaibaTemplateDefinitionList.add(definition1);

      } catch (Exception e) {
         throw new IllegalArgumentException("problem creating definition");
      }

      // OLT_CONTAINER_TEMPLATE_NAME = "FEX_RACK_001";
      // OLT_CONTAINER_CLASS_NAME = "Rack";
      //         
      // OLT_TEMPLATE_NAME = "OLT_NOKIA_01";
      // OLT_CLASS_NAME = "OpticalLineTerminal";
//      try {
//         KuwaibaTemplateDefinition definition1 = new KuwaibaTemplateDefinition();
//         definition1.setTemplateName("FEX_RACK_001");
//         definition1.setTemplateElementName("FEX_RACK_001");
//         definition1.setClassName("Rack");
//         definition1.setSpecial(false);
//
//         HashMap<String, String> definition1Attributes = new HashMap<String, String>();
//         definition1Attributes.put("rackUnits", "42");
//         definition1Attributes.put("rackUnitsNumberingDescending", "true");
//         definition1.getTemplateAttributes().putAll(definition1Attributes);
//
//         // 10 OLT in rack
//         for (int oltNo = 1; oltNo <= 10; oltNo++) {
//            KuwaibaTemplateDefinition childDefinition1 = new KuwaibaTemplateDefinition();
//            childDefinition1.setTemplateElementName("OLT_NOKIA_01_" + String.format("%03d", oltNo));
//            childDefinition1.setClassName("OpticalLineTerminal");
//            childDefinition1.setSpecial(false);
//
//            HashMap<String, String> childDefinition1Attributes = new HashMap<String, String>();
//            childDefinition1Attributes.put("rackUnits", "2");
//            childDefinition1Attributes.put("position", Integer.toString(2 + oltNo * 2)); // top 2 slots free
//            childDefinition1.getTemplateAttributes().putAll(childDefinition1Attributes);
//
//            // 2 PON cards per olt
//            for (int card = 1; card <= 2; card++) {
//               KuwaibaTemplateDefinition childDefinition1_1 = new KuwaibaTemplateDefinition();
//               childDefinition1_1.setTemplateElementName("card-" + String.format("%03d", card));
//               childDefinition1_1.setClassName("OLTBoard");
//               childDefinition1_1.setSpecial(false);
//               childDefinition1.getChildKuwaibaTemplateDefinitions().add(childDefinition1_1);
//
//               // 16 pon ports per card
//               for (int opticalPort = 1; opticalPort <= 16; opticalPort++) {
//                  KuwaibaTemplateDefinition childDefinition1_2 = new KuwaibaTemplateDefinition();
//                  childDefinition1_2.setTemplateElementName("PON-" + String.format("%03d", opticalPort));
//                  childDefinition1_2.setClassName("OpticalPort");
//                  childDefinition1_2.setSpecial(false);
//                  childDefinition1_1.getChildKuwaibaTemplateDefinitions().add(childDefinition1_2);
//               }
//
//            }
//            
//            // OLT management port
//            KuwaibaTemplateDefinition childDefinition1_2 = new KuwaibaTemplateDefinition();
//            childDefinition1_2.setTemplateElementName("management");
//            childDefinition1_2.setClassName("OpticalPort");
//            childDefinition1_2.setSpecial(false);
//
//            HashMap<String, String> childDefinition1_2_Attributes = new HashMap<String, String>();
//            childDefinition1_2_Attributes.put("isManagement", "true");
//            childDefinition1_2.getTemplateAttributes().putAll(childDefinition1_2_Attributes);
//            
//            childDefinition1.getChildKuwaibaTemplateDefinitions().add(childDefinition1_2);
//            
//            definition1.getChildKuwaibaTemplateDefinitions().add(childDefinition1);
//            
//         }
//
//         kuwaibaTemplateDefinitionList.add(definition1);
//
//      } catch (Exception e) {
//         throw new IllegalArgumentException("problem creating definition");
//      }

      // wire containers
      
//    // STREET_DUCT_1 street duct
//    // block to isolate local variables            
    try {
       KuwaibaTemplateDefinition definition1 = new KuwaibaTemplateDefinition();
       definition1.setTemplateName("STREET_DUCT_1");
       definition1.setClassName("WireContainer");
       definition1.setSpecial(false);

       kuwaibaTemplateDefinitionList.add(definition1);

    } catch (Exception e) {
       throw new IllegalArgumentException("problem creating definition");
    }

//      // BFU_1_2 blown fiber unit 1 cable, 2 cores coloured
//      // block to isolate local variables            
//      try {
//         KuwaibaTemplateDefinition definition1 = new KuwaibaTemplateDefinition();
//         definition1.setTemplateName("BFU_1_2");
//         definition1.setClassName("WireContainer");
//         definition1.setSpecial(false);
//         definition1.setTemplateFunction("ColoredFiberWireContainerFunction");
//
//         HashMap<String, String> attributes1 = new HashMap<String, String>();
//         attributes1.put("numberOfCables", "1");
//         attributes1.put("numberOfFibers", "2");
//         definition1.setTemplateFunctionAttributes(attributes1);
//
//         kuwaibaTemplateDefinitionList.add(definition1);
//
//      } catch (Exception e) {
//         throw new IllegalArgumentException("problem creating definition");
//      }
//
//      // BFU_1_4 blown fiber unit 1 cables, 4 cores  coloured
//      // block to isolate local variables            
//      try {
//         KuwaibaTemplateDefinition definition1 = new KuwaibaTemplateDefinition();
//         definition1.setTemplateName("BFU_1_4");
//         definition1.setClassName("WireContainer");
//         definition1.setSpecial(false);
//         definition1.setTemplateFunction("ColoredFiberWireContainerFunction");
//
//         HashMap<String, String> attributes1 = new HashMap<String, String>();
//         attributes1.put("numberOfCables", "4");
//         attributes1.put("numberOfFibers", "12");
//         definition1.setTemplateFunctionAttributes(attributes1);
//
//         kuwaibaTemplateDefinitionList.add(definition1);
//
//      } catch (Exception e) {
//         throw new IllegalArgumentException("problem creating definition");
//      }
//
//      // BFU_4_12 blown fiber unit 4 cables, 12 cores  coloured
//      // block to isolate local variables            
//      try {
//         KuwaibaTemplateDefinition definition1 = new KuwaibaTemplateDefinition();
//         definition1.setTemplateName("BFU_4_12");
//         definition1.setClassName("WireContainer");
//         definition1.setSpecial(false);
//         definition1.setTemplateFunction("ColoredFiberWireContainerFunction");
//
//         HashMap<String, String> attributes1 = new HashMap<String, String>();
//         attributes1.put("numberOfCables", "4");
//         attributes1.put("numberOfFibers", "12");
//         definition1.setTemplateFunctionAttributes(attributes1);
//
//         kuwaibaTemplateDefinitionList.add(definition1);
//
//      } catch (Exception e) {
//         throw new IllegalArgumentException("problem creating definition");
//      }

      pr.getKuwaibaTemplateList().addAll(kuwaibaTemplateDefinitionList);
      
      return pr;

   }

}



