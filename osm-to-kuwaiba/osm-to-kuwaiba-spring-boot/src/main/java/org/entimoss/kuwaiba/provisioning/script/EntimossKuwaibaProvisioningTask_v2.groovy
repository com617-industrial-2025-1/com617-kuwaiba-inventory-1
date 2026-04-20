/**
 * This Task script imports a Kuwaiba inventory from json files
 * Entimoss Ltd. -  (Apache Licensed)
 * Version 2.0.1
 * 
 * Parameters:
 *   
 *    String kuwaibaProvisioningRequisitionFileName File name and location directory of kuwaibaProvisioningRequisition. 
 *           Defaults to /external-data/kuwaibaProvisioningRequisition-data.json
 *    
 *    boolean createNewTemplates  if true (default) creates new template definitions if defined in provisioning file.
 *    
 *    boolean createNewObjects    if true (default) creates new object definitions if defined in provisioning file.
 *        
 *    boolean createNewConnectionOjects if true (default) creates new connection object definitions if defined in provisioning file.
 *
 * Applies to: Kuwaiba Task Script
 * 
 * Notes -  use COMMIT ON EXECUTE if running as a task
 * 
 * TODO Need to count created objects correctly
 * 
 */

package org.entimoss.kuwaiba.provisioning.script;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neotropic.kuwaiba.core.apis.persistence.ChangeDescriptor;
import org.neotropic.kuwaiba.core.apis.persistence.application.ActivityLogEntry;
import org.neotropic.kuwaiba.core.apis.persistence.application.ApplicationEntityManager;
import org.neotropic.kuwaiba.core.apis.persistence.application.TaskResult;
import org.neotropic.kuwaiba.core.apis.persistence.application.TemplateObject;
import org.neotropic.kuwaiba.core.apis.persistence.application.TemplateObjectLight;
import org.neotropic.kuwaiba.core.apis.persistence.exceptions.ApplicationObjectNotFoundException;
import org.neotropic.kuwaiba.core.apis.persistence.exceptions.BusinessObjectNotFoundException;
import org.neotropic.kuwaiba.core.apis.persistence.exceptions.InvalidArgumentException;
import org.neotropic.kuwaiba.core.apis.persistence.exceptions.InventoryException;
import org.neotropic.kuwaiba.core.apis.persistence.exceptions.MetadataObjectNotFoundException;
import org.neotropic.kuwaiba.core.apis.persistence.exceptions.OperationNotPermittedException;
import org.neotropic.kuwaiba.core.apis.persistence.metadata.MetadataEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neotropic.kuwaiba.core.apis.persistence.business.BusinessEntityManager;
import org.neotropic.kuwaiba.core.apis.persistence.business.BusinessObject;
import org.neotropic.kuwaiba.core.apis.persistence.business.BusinessObjectLight;
import org.neotropic.kuwaiba.core.apis.persistence.util.Constants;

import groovy.transform.CompileStatic; // enables static compilation - faster

// note use COMMIT ON EXECUTE
// uncomment in groovy script
EntimossKuwaibaProvisioningTask_v2 kuwaibaImport = new EntimossKuwaibaProvisioningTask_v2(bem, aem, mem, scriptParameters, connectionHandler);
return kuwaibaImport.runTask();

@CompileStatic
public class EntimossKuwaibaProvisioningTask_v2 {
   static Logger LOG = LoggerFactory.getLogger(EntimossKuwaibaProvisioningTask_v2.class);

   BusinessEntityManager bem = null; // injected in groovy
   ApplicationEntityManager aem = null; // injected in groovy
   MetadataEntityManager mem = null; // injected in groovy
   Map<String, String> parameters = null; // injected in groovy
   GraphDatabaseService connectionHandler = null; //injected in groovy

   int kuwaibaTemplatesExisting = 0;
   int kuwaibaTemplatesNew = 0;
   int kuwaibaClassesExisting = 0;
   int kuwaibaClassesNew = 0;
   
   public EntimossKuwaibaProvisioningTask_v2(BusinessEntityManager bem, ApplicationEntityManager aem, MetadataEntityManager mem, Map<String, String> scriptParameters, GraphDatabaseService connectionHandler) {
      super();
      this.bem = bem;
      this.aem = aem;
      this.mem = mem;
      this.parameters = (scriptParameters == null) ? new HashMap<String, String>() : scriptParameters;
      this.connectionHandler = connectionHandler;

   }

   public TaskResult runTask() {
      TaskResult taskResult = new TaskResult();

      taskResult.getMessages().add(TaskResult.createInformationMessage(
               String.format("running Script " + EntimossKuwaibaProvisioningTask_v2.class.getName() + " with parameters:" + parameters)));

      LOG.info("STARTING RUNNING SCRIPT " + EntimossKuwaibaProvisioningTask_v2.class.getName() + " with parameters:" + parameters);

      /*
       * kuwaibaProvisioningRequisitionFileName
       *    File name and location directory of kuwaibaProvisioningRequisition
       *    Defaults to /external-data/kuwaibaProvisioningRequisition-data.json
       */
      String kuwaibaProvisioningRequisitionFileName = parameters.getOrDefault("kuwaibaProvisioningRequisitionFileName", "/external-data/kuwaibaProvisioningRequisition-data.json");

      boolean createNewTemplates = Boolean.parseBoolean(parameters.getOrDefault("createNewTemplates", "true"));

      boolean createNewObjects = Boolean.parseBoolean(parameters.getOrDefault("createNewObjects", "true"));

      boolean createNewConnectionOjects = Boolean.parseBoolean(parameters.getOrDefault("createNewConnectionOjects", "true"));

      try {

         // TODO very large file file with multiple separate objects
         File kuwaibaProvisioningFile = new File(kuwaibaProvisioningRequisitionFileName);
         if (!kuwaibaProvisioningFile.exists()) {
            throw new IllegalArgumentException("sctipt cannot find file:" + kuwaibaProvisioningFile);
         }

         ObjectMapper om = new ObjectMapper();

         KuwaibaProvisioningRequisition kuwaibaProvisioningRequisition = om.readValue(kuwaibaProvisioningFile, KuwaibaProvisioningRequisition.class);

         LOG.info("Starting to load requistionFile " + kuwaibaProvisioningFile.getAbsolutePath() + " containing " + kuwaibaProvisioningRequisition.getKuwaibaTemplateList().size() +
                  " templates and" + kuwaibaProvisioningRequisition.getKuwaibaClassList().size() + " classes");

         //create new templates
         if (createNewTemplates) {
            LOG.info("STARTING CREATING TEMPLATES");
            createTemplates(kuwaibaProvisioningRequisition.getKuwaibaTemplateList());
            LOG.info("FINISHED CREATING TEMPLATES");
         } else
            LOG.info("CREATING TEMPLATES DISABLED");

         // create new objects
         if (createNewObjects) {
            LOG.info("STARTING CREATING NEW OBJECT CLASSES");
            createObjects(kuwaibaProvisioningRequisition.getKuwaibaClassList());
            LOG.info("FINISHED CREATING NEW OBJECT CLASSES");
         } else
            LOG.info("CREATING OBJECTS DISABLED");

      // create new connection objects
         if (createNewConnectionOjects) {
            LOG.info("STARTING CREATING CONNECTION OBJECTS");
            createConnections(kuwaibaProvisioningRequisition.getKuwaibaConnectionList());
            LOG.info("FINISHED CREATING CONNECTION OBJECTS");
         } else
            LOG.info("CREATING CONNECTION OBJECTS DISABLED");
         
      } catch (Exception ex) {
         LOG.error("problem running task", ex);
         taskResult.getMessages().add(TaskResult.createErrorMessage(
                  String.format("error running task " + ex)));
      }
      
      String msg = "End of task Script " + EntimossKuwaibaProvisioningTask_v2.class.getName() + " used existing templates: " + kuwaibaTemplatesExisting +
               " newTemplates: "+ kuwaibaTemplatesNew +" existingClasses: "+ kuwaibaClassesExisting +" new Classes:"+ kuwaibaClassesNew;

      taskResult.getMessages().add(TaskResult.createInformationMessage(msg));

      LOG.info(msg);

      return taskResult;
   }


   public void createObjects(List<KuwaibaClass> kuwaibaClassList) {
      for (KuwaibaClass kuwaibaClass : kuwaibaClassList) {
         if(LOG.isTraceEnabled()) LOG.trace("PROCESSING kuwaibaClass: " + kuwaibaClass);

         String createObjectClassName = kuwaibaClass.getClassName();
         String createObjectName = kuwaibaClass.getName();
         List<KuwaibaClass> parentClasses = kuwaibaClass.getParentClasses();
         String templateName = kuwaibaClass.getTemplateName();
         HashMap<String, String> initialAttributes = kuwaibaClass.getAttributes();

         BusinessObject businessObject = createClassIfDoesntExist(createObjectClassName, createObjectName,
                  parentClasses, templateName, initialAttributes);

         if(LOG.isDebugEnabled()) LOG.debug("FINISHED PROCESSING kuwaibaClass:" + kuwaibaClass + " MATCHING business object: " + businessObjectToString(businessObject));

      }

      LOG.info("Templates new: " + kuwaibaTemplatesExisting + " existing: " + kuwaibaTemplatesNew +
               " Classes: new: " + kuwaibaClassesExisting + " existing: " + kuwaibaClassesNew);

   }

 /**
    * creates new object under parent if object doesn't exist
    * return BusinessObject of existing object or new object if does already exist
    * @param createObjectClassName
    * @param createObjectName
    * @param parentObjectId
    * @param parentObjectClass
    * @return
    */
   public BusinessObject createClassIfDoesntExist(String createObjectClassName, String createObjectName, List<KuwaibaClass> parentClasses, String templateName, HashMap<String, String> initialAttributes) {

      BusinessObject createdObject = null;
      BusinessObject parentObject = null;

      // check if object already exists
      try {
         // see if there is an object with the same name
         List<BusinessObject> foundObjects = bem.getObjectsWithFilter(createObjectClassName, Constants.PROPERTY_NAME, createObjectName);
         if (!foundObjects.isEmpty()) {
            createdObject = foundObjects.get(0);
            if(LOG.isTraceEnabled()) LOG.trace("createIfDoesntExist - OBJECT ALREADY EXIST " + businessObjectToString(createdObject));
            kuwaibaClassesExisting++;
            return createdObject;
         }
      } catch (Exception ex) {
         throw new RuntimeException ("problem finding existing object:", ex);
      }

      // check if parent object exists
      try {
         
         parentObject = findDirectParentClass( parentClasses);
         
      } catch (Exception ex) {
         throw new RuntimeException ("createIfDoesntExist - problem finding parent object:", ex);
      }

      // check if template exists if not null
      
      TemplateObjectLight template = null;
      String templateId = null;

      String existingChildId = null;
      
      HashMap<String, String> newAttributes = (initialAttributes == null) ? new HashMap<String, String>() : new HashMap<String, String>(initialAttributes);
      newAttributes.put(Constants.PROPERTY_NAME, createObjectName);

      if (templateName != null && !templateName.isEmpty()) {
         try {

            // check if child of parent has been made with the same template (i.e. its name starts with the template name). 
            // If it does, then we want to use this object and update it's properties rather than create a new object
            List<BusinessObject> childObjects = bem.getChildrenOfClass(parentObject.getId(), parentObject.getClassName(), createObjectClassName, 0, 0);
            for (BusinessObject child : childObjects) {
               if (child.getName().startsWith(templateName)) {
                  existingChildId = child.getId();
                  createdObject = child;
                  
                  if(LOG.isTraceEnabled()) LOG.trace("createIfDoesntExist - matched templateName=" + templateName + " child object=" + businessObjectToString(child) + " will be updated in parent " +
                               businessObjectToString(parentObject));
                  
                  ChangeDescriptor changeDescriptor = bem.updateObject(child.getClassName(), existingChildId, newAttributes);
                  if(LOG.isTraceEnabled()) LOG.trace("createIfDoesntExist - updated child object name ChangeDescriptor [getAffectedProperties()=" +
                           changeDescriptor.getAffectedProperties() + ", getOldValues()=" +
                           changeDescriptor.getOldValues() + ", getNewValues()=" + changeDescriptor.getNewValues() +
                           ", getNotes()=" + changeDescriptor.getNotes() + "]");
                  
                  break;
               }
            }

            // if there is no existing candidate object, find the template to create a new object 
            if (existingChildId == null) {
               
            // see if there is a template with the template name
            List<TemplateObjectLight> foundTemplates = aem.getTemplatesForClass(createObjectClassName);
            for (TemplateObjectLight tmplate : foundTemplates) {
               if (templateName.equals(tmplate.getName())) {
                  template = tmplate;
                  templateId = template.getId();
                     if(LOG.isTraceEnabled()) LOG.trace("createIfDoesntExist - creating object " + template.getClassName() + " with template: " + template.getName() + " templateId: " + template.getId());
                  break;
               }
            }
            if (template == null) {
               throw new IllegalArgumentException("cannot find template for createObjectClassName " + createObjectClassName + " template name: " + templateName);
            }
            }
         } catch (Exception ex) {
            LOG.error("problem finding template:", ex);
         }
      }

      if (createdObject == null) {
         // create new object with parent
         try {

            String createdObjectId = bem.createObject(createObjectClassName, parentObject.getClassName(), parentObject.getId(), newAttributes, templateId);

            // this is added because the created object takes the name of the template and not the name we want to give it.
            ChangeDescriptor changeDescriptor = bem.updateObject(createObjectClassName, createdObjectId, newAttributes);
            if(LOG.isTraceEnabled()) LOG.trace("createIfDoesntExist - updated new object name  ChangeDescriptor [getAffectedProperties()=" + changeDescriptor.getAffectedProperties() + ", getOldValues()=" +
                     changeDescriptor.getOldValues() + ", getNewValues()=" + changeDescriptor.getNewValues() + ", getNotes()=" + changeDescriptor.getNotes() + "]");

            createdObject = bem.getObject(createObjectClassName, createdObjectId);

            // TODO WORK AROUND changes splitter input port names WHICH ARE INCORRECTLY LABELED IN OpticalSplitter TEMPLATE
            // CANNOT CHANGE TEMPLATE because transaction not closed
            if (templateId != null && !templateId.isEmpty()) {

               List<BusinessObject> opticalPorts = bem.getObjectsWithFilter("OpticalPort", "name", "001-IN");
               for (BusinessObject port : opticalPorts) {
                  //TODO isParent(String parentClass, String parentId, String childClass, String childId) - doesnt work - TRANSACTION IS NOT CLOSED
                  List<BusinessObjectLight> portParents = bem.getParentsUntilFirstOfClass("OpticalPort", port.getId(), "OpticalSplitter");
                  if (!portParents.isEmpty()) {
                     if(LOG.isDebugEnabled()) LOG.debug("createIfDoesntExist - updating OpticalSplitter IN port " + port.getId() + " name from 001-IN to IN-001");
                  HashMap<String, String> attributes = new HashMap<String, String>();
                  attributes.put("name", "IN-001");
                  bem.updateObject("OpticalPort", port.getId(), attributes);
               }
            }
            }

            if(LOG.isTraceEnabled()) LOG.trace("createIfDoesntExist - FINISHED CREATING NEW OBJECT " + businessObjectToString(createdObject));

            kuwaibaClassesNew++;
         } catch (Exception e) {
            LOG.error("problem creating object createObjectClass " + createObjectClassName +
                     ", createObjectName:" + createObjectName + ", parentOid:" + parentObject.getId() + 
                     ", parentClassName " + parentObject.getClassName(), e);
         }
      }

      return createdObject;

   }
   
   /**
    * if KuwaibaClass searchCass has parents defined, find the parents and then find the class by name from among the children of parents.
    * If KuwaibaClass does not have parents, try to find the class just by its name.
    * @param searchClass
    * @return null if not found
    * @Throws exception if parents not null but do not exist
    */
   public BusinessObject findObjectWithParents(KuwaibaClass searchClass) {

      BusinessObject foundObject = null;

      try {

         // find parent objects if specified
         BusinessObject parentObject = findDirectParentClass(searchClass.getParentClasses());
         if(LOG.isTraceEnabled()) LOG.trace("findObjectWithParents directParent=" + businessObjectToString(parentObject));

         List<BusinessObject> foundObjects;
         if (parentObject != null) {
            
            if (searchClass.getSpecial()) {
               List<BusinessObjectLight> foundLightObjects = bem.getSpecialChildrenOfClassLight(parentObject.getId(), parentObject.getClassName(), searchClass.getClassName(), -1);
               if(LOG.isTraceEnabled()) LOG.trace("findObjectWithParents parentObject.getId()=" + parentObject.getId() + ", parentObject.getClassName()=" + parentObject.getClassName() +
                        ", searchClass.getClassName()=" + searchClass.getClassName() + ", searchClass.getSpecial()=" + searchClass.getSpecial() + ", foundLightObjects=" + foundLightObjects);

               // try to find by full name
               for (BusinessObjectLight child : foundLightObjects) {
                  if (child.getName().equals(searchClass.getName())) {
                     foundObject = bem.getObject(child.getClassName(), child.getId() ) ;
                     break;
                  }
               }

               // alternatively try to find find by template name if defined
               if (searchClass.getTemplateName() != null && !searchClass.getTemplateName().isEmpty()) {
                  for (BusinessObjectLight child : foundLightObjects) {
                     if (child.getName().equals(searchClass.getTemplateName())) {
                        foundObject = bem.getObject(child.getClassName(), child.getId());
                        break;
                     }
                  }
               }

            } else {
            foundObjects = bem.getChildrenOfClass(parentObject.getId(), parentObject.getClassName(), searchClass.getClassName(), 0, 0);
               if(LOG.isTraceEnabled()) LOG.trace("findObjectWithParents parentObject.getId()=" + parentObject.getId() + ", parentObject.getClassName()=" + parentObject.getClassName() +
                        ", searchClass.getClassName()=" + searchClass.getClassName() + ", searchClass.getSpecial()=" + searchClass.getSpecial() + ", foundObjects=" + foundObjects);
               // try to find by full name
            for (BusinessObject child : foundObjects) {
               if (child.getName().equals(searchClass.getName())) {
                  foundObject = child;
                  break;
               }
            }

               // alternatively try to find find by template name if defined
               if (searchClass.getTemplateName() != null && !searchClass.getTemplateName().isEmpty()) {
                  for (BusinessObject child : foundObjects) {
                     if (child.getName().equals(searchClass.getTemplateName())) {
                        foundObject = bem.getObject(child.getClassName(), child.getId());
                        break;
                     }
                  }
               }

         }

         } else {
            foundObjects = bem.getObjectsWithFilter(searchClass.getClassName(), Constants.PROPERTY_NAME, searchClass.getName());
            if (!foundObjects.isEmpty()) {
               foundObject = foundObjects.get(0);

            }
         }

         if(LOG.isTraceEnabled()) LOG.trace("findObjectWithParents found object =" + businessObjectToString(foundObject));
         return foundObject;

      } catch (Exception ex) {
         throw new RuntimeException("problem finding existing object:", ex);
      }
   }

/**
 * searches for parents in order of parentClasses list. 
 * @param parentClasses
 * @return parent class from the hierarchy
 */
   public BusinessObject findDirectParentClass(List<KuwaibaClass> parentClasses) {

      if (parentClasses == null || parentClasses.isEmpty())
         return null;

      BusinessObject parentObject = null;

      try {

         Iterator<KuwaibaClass> parentIterator = parentClasses.iterator();

         // find first object in parent list
         KuwaibaClass parentClass = parentIterator.next();
         if(LOG.isTraceEnabled()) LOG.trace("findDirectParentClass looking for first parentObject " + parentClass);
         
         List<BusinessObject> foundObjects = bem.getObjectsWithFilter(parentClass.getClassName(), Constants.PROPERTY_NAME, parentClass.getName());
         if (!foundObjects.isEmpty()) {
            parentObject = foundObjects.get(0);
            if(LOG.isTraceEnabled()) LOG.trace("findDirectParentClass first parentObject exists " + businessObjectToString(parentObject));
         } else {
            throw new IllegalArgumentException("cannot find parent class " + parentClass.getClassName() + " name " + parentClass.getName());
         }

         // iterate parent list to find any children
         while (parentIterator.hasNext()) {
            parentClass = parentIterator.next();
            if(LOG.isTraceEnabled()) LOG.trace("findDirectParentClass looking for next parentObject " + parentClass);

            BusinessObject foundObject = null;

            foundObjects = bem.getChildrenOfClass(parentObject.getId(), parentObject.getClassName(), parentClass.getClassName(), 0, 0);
            for (BusinessObject businessObject : foundObjects) {
               if (businessObject.getName().equals(parentClass.getName())) {
                  foundObject = businessObject;
                  if(LOG.isTraceEnabled()) LOG.trace("findDirectParentClass found parent child " + businessObjectToString(foundObject));
                  break;
               }
            }

            if (foundObject == null)
               throw new IllegalArgumentException("cannot find parent class from listed parent " + parentClass.getClassName() +
                        " name " + parentClass.getName() + " for kuwaiba parent object id" + parentObject.getId() +
                        " parent object class " + parentObject.getClassName());
            
            parentObject = foundObject;
         }

      } catch (Exception ex) {
         throw new IllegalArgumentException("createIfDoesntExist - problem finding parent object:", ex);
      }

      return parentObject;
   }
   
   

   /*
    * TEMPLATE CREATION METHODS
    */
   
   public int createChildTemplateElements(List<KuwaibaTemplateDefinition> kuwaibaChildTemplateElementList, String elementParentClassName, String elementParentId) {
      int templateElementsCreated = 0;

      if(LOG.isDebugEnabled()) LOG.debug("creating " + kuwaibaChildTemplateElementList.size() + " child template elements");
      for (KuwaibaTemplateDefinition templateElement : kuwaibaChildTemplateElementList) {

         String function = templateElement.getTemplateFunction();

         if (function == null || function.isEmpty()) {

            try {
               String clildId = null;
               if (templateElement.getSpecial()) {
                  //.createTemplateSpecialElement(String tsElementClass, String tsElementParentClassName, String tsElementParentId, String tsElementName)
                  clildId = aem.createTemplateSpecialElement(templateElement.getClassName(), elementParentClassName, elementParentId,
                           templateElement.getTemplateElementName());
               } else {
                  clildId = aem.createTemplateElement(templateElement.getClassName(), elementParentClassName, elementParentId, templateElement.getTemplateElementName());
               }

               // add attributes
               //ChangeDescriptor updateTemplateElement(String templateElementClass, String templateElementId, String[] attributeNames, String[] attributeValues)
               ArrayList<String> attributeNames = new ArrayList<String>();
               ArrayList<String> attributeValues = new ArrayList<String>();
               for (String key : templateElement.getTemplateAttributes().keySet()) {
                  String value = templateElement.getTemplateAttributes().get(key);
                  attributeNames.add(key);
                  attributeValues.add(value);
               }

               if(LOG.isDebugEnabled()) LOG.debug("created template child:" + templateElement + ", clildId=" + clildId + " template element for elementParentClassName=" +
                        elementParentClassName + ", elementParentId=" + elementParentId);

               ChangeDescriptor changeDescriptor = aem.updateTemplateElement(templateElement.getClassName(), clildId,
                        (String[]) attributeNames.toArray(), (String[]) attributeValues.toArray());
               if(LOG.isTraceEnabled()) LOG.trace("added properties to child ChangeDescriptor [getAffectedProperties()=" +
                        changeDescriptor.getAffectedProperties() + ", getOldValues()=" +
                        changeDescriptor.getOldValues() + ", getNewValues()=" + changeDescriptor.getNewValues() +
                        ", getNotes()=" + changeDescriptor.getNotes() + "]");

               templateElementsCreated++;

               if (templateElement.getChildKuwaibaTemplateDefinitions() != null && !templateElement.getChildKuwaibaTemplateDefinitions().isEmpty()) {
                  templateElementsCreated = templateElementsCreated + createChildTemplateElements(templateElement.getChildKuwaibaTemplateDefinitions(), templateElement.getClassName(), clildId);
               }

            } catch (Exception ex) {
               throw new IllegalArgumentException("problem creating child template element for elementParentClassName" +
                        elementParentClassName + ", elementParentId" + elementParentId + " child:" + templateElement, ex);
            }

         } else {
            try {

               HashMap<String, String> functionAttributes = templateElement.getTemplateFunctionAttributes();

               if(LOG.isTraceEnabled()) LOG.trace("trying to create template elements from function=" + function + " with functionAttributes=" +
                        functionAttributes + " className=" +
                        templateElement.getClassName() + "  templateElementParentClassName=" + elementParentClassName +
                        "  templateElementParentId=" + elementParentId);

               templateElementsCreated = templateElementsCreated + createTemplateElementsFromFunction(null, templateElement.getTemplateElementName(), elementParentClassName, elementParentId, function, functionAttributes);

            } catch (Exception ex) {
               throw new IllegalArgumentException("problem creating child template element from function for elementParentClassName=" +
                        elementParentClassName + ", elementParentId=" + elementParentId + " child:" + templateElement, ex);
            }

         }
      }

      return templateElementsCreated;

   }

   public int createTemplateElementsFromFunction(String templateName, String templateElementName, String templateElementParentClassName, String templateElementParentId, String function,
            HashMap<String, String> functionAttributes) {
      int elementsCreated = 0;

      if(LOG.isTraceEnabled()) LOG.trace("trying to create new template element (templateName=" + templateName + ") for parent class=" + templateElementParentClassName +
               " id=" + templateElementParentId +
               " from function=" + function + " with functionAttributes=" + functionAttributes);

      try {
         switch (function) {

         case "FiberSplitterFunction":
            elementsCreated = elementsCreated + createOpticalFiberSplitterTemplateElements(templateName, templateElementName, templateElementParentClassName, templateElementParentId, functionAttributes);
            break;

         case "OpticalSpliceBoxFunction":
            elementsCreated = elementsCreated + createOpticalSpliceBoxTemplateElements(templateName, templateElementName, templateElementParentClassName, templateElementParentId, functionAttributes);
            break;

         case "ColoredFiberWireContainerFunction":
            elementsCreated = elementsCreated + createColoredOpticalFiberContainerTemplateElements(templateName, templateElementName, templateElementParentClassName, templateElementParentId, functionAttributes);
            break;

         default:
            throw new IllegalArgumentException("template function does not exist: " + function);
         }
      } catch (Exception ex) {
         throw new IllegalArgumentException("problem creating child template element from function", ex);
      }

      return elementsCreated;
   }

   public int createTemplates(List<KuwaibaTemplateDefinition> kuwaibaTemplateDefinitionList) {

      int templateElementsCreated = 0;

      for (KuwaibaTemplateDefinition kuwaibaTemplateDefinition : kuwaibaTemplateDefinitionList) {

         String className = kuwaibaTemplateDefinition.getClassName();

         HashMap<String, String> functionAttributes = kuwaibaTemplateDefinition.getTemplateFunctionAttributes();

         List<KuwaibaTemplateDefinition> childKuwaibaTemplateElements = kuwaibaTemplateDefinition.getChildKuwaibaTemplateDefinitions();

         // if templateName is set, this is a top level template and templateElementName must not be set
         // template functions can only be called for top level templates
         String templateName = kuwaibaTemplateDefinition.getTemplateName();

         String templateElementName = kuwaibaTemplateDefinition.getTemplateElementName();

         // templateName  must be set
         if ((templateName == null || !templateName.isEmpty())) {
            new IllegalArgumentException("templateName must be set");
         }
         if ((className == null || !className.isEmpty())) {
            new IllegalArgumentException("className must be set");
         }

         // create new template
         String templateId = null;

         try {
            // check if template already exists 
            List<TemplateObjectLight> foundTemplates = aem.getTemplatesForClass(className);
            for (TemplateObjectLight tmplate : foundTemplates) {
               if (templateName.equals(tmplate.getName())) {
                  templateId = tmplate.getId();
                  break;
               }
            }

            if (templateId != null) {
               if(LOG.isTraceEnabled()) LOG.trace("TEMPLATE " + templateName + " ALREADY EXISTS, will not create a new template with templateId=" + templateId);

            } else {
               // if template doesn't exist

               String function = kuwaibaTemplateDefinition.getTemplateFunction();

               if (function == null || function.isEmpty()) {
                  if(LOG.isTraceEnabled()) LOG.trace("trying to create without function new template name = " + templateName);

                  // no function so create a simple template for this class
                  templateId = aem.createTemplate(className, templateName);
                  templateElementsCreated++;
                  if(LOG.isTraceEnabled()) LOG.trace("template " + templateName + " Was created. New templateId=" + templateId);

                  // add attributes
                  //ChangeDescriptor updateTemplateElement(String templateElementClass, String templateElementId, String[] attributeNames, String[] attributeValues)
                  ArrayList<String> attributeNames = new ArrayList<String>();
                  ArrayList<String> attributeValues = new ArrayList<String>();
                  for (String key : kuwaibaTemplateDefinition.getTemplateAttributes().keySet()) {
                     String value = kuwaibaTemplateDefinition.getTemplateAttributes().get(key);
                     attributeNames.add(key);
                     attributeValues.add(value);
                  }

                  ChangeDescriptor changeDescriptor = aem.updateTemplateElement(kuwaibaTemplateDefinition.getClassName(), templateId,
                           (String[]) attributeNames.toArray(), (String[]) attributeValues.toArray());
                  if(LOG.isTraceEnabled()) LOG.trace("added properties to template ChangeDescriptor [getAffectedProperties()=" +
                           changeDescriptor.getAffectedProperties() + ", getOldValues()=" +
                           changeDescriptor.getOldValues() + ", getNewValues()=" + changeDescriptor.getNewValues() +
                           ", getNotes()=" + changeDescriptor.getNotes() + "]");

                  if (childKuwaibaTemplateElements != null && !childKuwaibaTemplateElements.isEmpty()) {
                     // recursively create child templates
                     templateElementsCreated = templateElementsCreated + createChildTemplateElements(childKuwaibaTemplateElements, className, templateId);

                  }

               } else {

                  if(LOG.isTraceEnabled()) LOG.trace("trying to create new template " + templateName + " with class name=" + className + " from function=" + function + " with functionAttributes=" + functionAttributes);

                  templateElementsCreated = templateElementsCreated + createTemplateElementsFromFunction(templateName, templateElementName, null, null,  function, functionAttributes);

                  if(LOG.isDebugEnabled()) LOG.debug("template " + templateName + " was created using function " + function + " new templateId=" + templateId);

               }

            }

         } catch (Exception e) {
            throw new IllegalArgumentException("problem creating template name " + templateName, e);
         }

      }

      return templateElementsCreated;

   }

   public int createOpticalFiberSplitterTemplateElements(String templateName, String templateElementName, String templateElementParentClassName, String templateElementParentId,
            HashMap<String, String> functionAttributes) {

      int elementsCreated = 0;

      if (functionAttributes.get("numberOfPorts") == null)
         throw new IllegalArgumentException("OpticalSplitterFunction number of ports not set ");

      try {
         Integer numberOfPorts = Integer.parseInt(functionAttributes.get("numberOfPorts"));
         
         // if templateName!= null then this is the top of the tree so create a template 
         String elementId = templateElementParentId;
         
         if (templateName!=null) {
            elementId = aem.createTemplate("FiberSplitter", templateName);
         } else {
            String nameOfTemplateElement = (templateElementName !=null ) ? templateElementName : "Splitter";
            elementId = aem.createTemplateElement("FiberSplitter", templateElementParentClassName, templateElementParentId, nameOfTemplateElement);
         }

         // TODO MIRROR PORT FUNCTION CREATES IN PORT WITH WRONG NAME
         // TODO - FIX TEMPLATE ENGINE - splitter must start with IN
         String templateElementNamePattern = "[multiple-mirror(1," + numberOfPorts + ")]";

         // CANT USE UPDATE TEMPLATE BECAUSE getTemplateEement fails as does not close transaction
         // updateTemplate updateTemplateElement(String templateElementClass, String templateElementId, String[] attributeNames, String[] attributeValues)

         List<String> childTemplateElementIds = Arrays
                  .asList(aem.createBulkTemplateElement("OpticalPort", "FiberSplitter", elementId, templateElementNamePattern));

         elementsCreated = childTemplateElementIds.size();

         for (String childId : childTemplateElementIds) {
            if(LOG.isTraceEnabled()) LOG.trace("created splitter optical port template element  id=" + childId);

            // fails because getTemplateElement does not close transaction
            //TemplateObject templateObject = aem.getTemplateElement("OpticalPort", childId);
            // if(LOG.isTraceEnabled()) LOG.trace("created splitter optical port name "+templateObject.getName()+" id=" +templateObject.getId());
         }

         return elementsCreated;

      } catch (Exception e) {
         throw new IllegalArgumentException("problem creating splitter ports for parentClassName=" +
                  templateElementParentClassName + " parentId=" + templateElementParentId, e);
      }

   }

   public int createOpticalSpliceBoxTemplateElements(String templateName, String templateElementName, String templateElementParentClassName, String templateElementParentId,
            HashMap<String, String> functionAttributes) {

      int elementsCreated = 0;

      if (functionAttributes.get("numberOfPorts") == null)
         throw new IllegalArgumentException("SpliceBoxFunction number of ports not set ");

      try {
         Integer numberOfPorts = Integer.parseInt(functionAttributes.get("numberOfPorts"));
         
         // if templateName!= null then this is the top of the tree so create a template 
         String elementId = templateElementParentId;
         
         if (templateName!=null) {
            elementId = aem.createTemplate("SpliceBox", templateName);
         } else {
            String nameOfTemplateElement = (templateElementName !=null ) ? templateElementName : "SpliceBox";
            elementId = aem.createTemplateElement("SpliceBox", templateElementParentClassName, templateElementParentId, nameOfTemplateElement);
         }

         String templateElementNamePattern = "[mirror(1," + numberOfPorts + ")]";

         // String templateElementClassName, String templateElementParentClassName, String templateElementParentId, String templateElementNamePattern
         List<String> childTemplateElementIds = Arrays
                  .asList(aem.createBulkTemplateElement("OpticalPort", "SpliceBox", elementId , templateElementNamePattern));

         elementsCreated = childTemplateElementIds.size();

         for (String childId : childTemplateElementIds) {
            if(LOG.isTraceEnabled()) LOG.trace("created SpliceBox optical port template element  id=" + childId);

            // fails because getTemplateElement does not close transaction
            //            TemplateObject templateObject = aem.getTemplateElement("OpticalPort", childId);
            //            if(LOG.isTraceEnabled()) LOG.trace("created splitter optical port name "+templateObject.getName()+" id=" +templateObject.getId());
         }

         return elementsCreated;

      } catch (Exception e) {
         throw new IllegalArgumentException("problem creating splice ports for parentClassName=" +
                  templateElementParentClassName + " parentId=" + templateElementParentId, e);
      }

   }

   public int createColoredOpticalFiberContainerTemplateElements(String templateName, String templateElementName, String templateElementParentClassName, String templateElementParentId,
            HashMap<String, String> functionAttributes) {

      int elementsCreated = 0;

      if (functionAttributes.get("numberOfCables") == null)
         throw new IllegalArgumentException("OpticalFiberContainerTemplateFunction numberOfCables not set ");
      if (functionAttributes.get("numberOfFibers") == null)
         throw new IllegalArgumentException("OpticalFiberContainerTemplateFunction numberOfFibers not set ");

      Integer numberOfCables = Integer.parseInt(functionAttributes.get("numberOfCables"));
      Integer numberOfFibers = Integer.parseInt(functionAttributes.get("numberOfFibers"));

      try {
         
         // if templateName!= null then this is the top of the tree so create a template 
         String elementId = templateElementParentId;
         
         if (templateName!=null) {
            elementId = aem.createTemplate("WireContainer", templateName);
         } else {
            String nameOfTemplateElement = (templateElementName !=null ) ? templateElementName :  "WireContainer";
            elementId = aem.createTemplateElement("WireContainer", templateElementParentClassName, templateElementParentId, nameOfTemplateElement);
         }
         
         for (int cableNo = 1; cableNo <= numberOfCables; cableNo++) {
            String cableName = String.format("%02d", cableNo) + "-" + ContainerColour.getColourForStrand(cableNo);

            // .createTemplateSpecialElement(String tsElementClass, String tsElementParentClassName, String tsElementParentId, String tsElementName)
            String cableObjectId = aem.createTemplateSpecialElement("WireContainer", "WireContainer", elementId, cableName);
            if(LOG.isTraceEnabled()) LOG.trace("created cable id=" + cableObjectId + " cable name=" + cableName);

            // create fibers inside cable
            for (int fiberNo = 1; fiberNo <= numberOfFibers; fiberNo++) {
               String fiberName = String.format("%02d", fiberNo) + "-" + ContainerColour.getColourForStrand(fiberNo);
               String opticalLinkObjectId = aem.createTemplateSpecialElement("OpticalLink", "WireContainer", cableObjectId, fiberName);
               if(LOG.isTraceEnabled()) LOG.trace("created optical link id=" + opticalLinkObjectId + " cable name=" + fiberName);
               elementsCreated++;
            }

         }

         return elementsCreated;

      } catch (Exception e) {
         throw new IllegalArgumentException("problem creating wire container for parentClassName=" +
                  templateElementParentClassName + " parentId=" + templateElementParentId, e);
      }

   }


   // overloaded toString methods for BusinessObjects
   String businessObjectToString(BusinessObject bo) {
      return (bo == null) ? "BusinessObject[ null ]"
               : "BusinessObject[ getName()=" + bo.getName() + ", getClassName()=" + bo.getClassName() + ", getClassDisplayName()=" +
                        bo.getClassDisplayName() + ", getId()=" + bo.getId() + " getAttributes()=" + bo.getAttributes() + "]";
   }

   String businessObjectToString(BusinessObjectLight bo) {
      return (bo == null) ? "BusinessObjectLight[ null ]"
               : "BusinessObjectLight[ getName()=" + bo.getName() + ", getClassName()=" + bo.getClassName() + ", getClassDisplayName()=" +
                        bo.getClassDisplayName() + ", getId()=" + bo.getId() + "]";
   }

   /*
    * CONNECTION METHODS
    */

   public void createConnections(List<KuwaibaConnection> containerConnectionList) {
      // create the connection manager
      PhysicalConnectionsServiceProxy physicalConnectionService = new PhysicalConnectionsServiceProxy(aem, bem, mem);
      // create new connections
      for (KuwaibaConnection kuwaibaConnection : containerConnectionList) {
         if(LOG.isTraceEnabled()) LOG.trace("creating connection using: " + kuwaibaConnection);

         String name = kuwaibaConnection.getConnectionClass().getName();
         String connectionClassName = kuwaibaConnection.getConnectionClass().getClassName();
         String connectionTemplateName = kuwaibaConnection.getConnectionClass().getTemplateName();
         List<KuwaibaClass> connectionParentClasses = kuwaibaConnection.getConnectionClass().getParentClasses();

         // check if a and and b objects exist
         BusinessObject aObject = null;
         BusinessObject bObject = null;
         try {
            
            aObject = findObjectWithParents(kuwaibaConnection.getEndpointA());
            
            bObject = findObjectWithParents(kuwaibaConnection.getEndpointB());
            
         } catch (Exception ex) {
            LOG.error("problem finding a and b end objects:", ex);
         }
         if (aObject == null || bObject == null)
            throw new IllegalArgumentException("ends of connection cannot be null:  aObject=" + aObject + " from endpointA=" + kuwaibaConnection.getEndpointA() + 
                     "  bObject=" + bObject+ " from endpointB=" + kuwaibaConnection.getEndpointB() );

         
         if (connectionParentClasses != null && !connectionParentClasses.isEmpty()) {
            // if there are parent objects, then associate the found container/link with the A and B endpoints.
            // associate end objects with containment object if set
            
            try {

               // check if containment object exists
               BusinessObject physicalConnectionObject = findObjectWithParents(kuwaibaConnection.getConnectionClass());
               if (physicalConnectionObject == null)
                  throw new IllegalArgumentException("cannot find " + kuwaibaConnection.getConnectionClass());

               if(LOG.isTraceEnabled()) LOG.trace("found physicalConnectionObject " + this.businessObjectToString(physicalConnectionObject)
                        + "for connection class definition " + kuwaibaConnection.getConnectionClass());

               // check if endpoints are connected
               Map<String, List<BusinessObjectLight>> attrs = bem.getSpecialAttributes(physicalConnectionObject.getClassName(), physicalConnectionObject.getId(), "endpointA", "endpointB");

               BusinessObjectLight currentEndpointA = null;
               BusinessObjectLight currentEndpointB = null;

               if (attrs.containsKey("endpointA"))
                  currentEndpointA = (BusinessObjectLight) attrs.get("endpointA").get(0);

               if (attrs.containsKey("endpointB"))
                  currentEndpointB = (BusinessObjectLight) attrs.get("endpointB").get(0);

               if (currentEndpointA == null && currentEndpointB == null) {

                  // from EditConnectionsVisualAction.java
                  // bem.createSpecialRelationship(selectedPhysicalConnection.getClassName(), selectedPhysicalConnection.getId(),
                  //        selectedEndPointA.getClassName(), selectedEndPointA.getId(), endpointAName, true);
                  String endpointAName = "endpointA", endpointBName = "endpointB";

                  bem.createSpecialRelationship(physicalConnectionObject.getClassName(), physicalConnectionObject.getId(),
                           aObject.getClassName(), aObject.getId(), endpointAName, true);

                  bem.createSpecialRelationship(physicalConnectionObject.getClassName(), physicalConnectionObject.getId(),
                           bObject.getClassName(), bObject.getId(), endpointBName, true);
                  
                  // change name of connection from template name to new defined name
                  if (physicalConnectionObject.getName().equals(connectionTemplateName)) {
                     HashMap<String, String> newAttributes = new HashMap<String, String>();
                     newAttributes.put(Constants.PROPERTY_NAME, kuwaibaConnection.getConnectionClass().getName());
                     ChangeDescriptor changeDescriptor = bem.updateObject(physicalConnectionObject.getClassName(), physicalConnectionObject.getId(), newAttributes);
                     if(LOG.isTraceEnabled()) LOG.trace("changed name of connection from " + connectionTemplateName + " to " + kuwaibaConnection.getConnectionClass().getName());
                  }

                  if(LOG.isTraceEnabled()) LOG.trace("Created new end point connections for connection definition=" + kuwaibaConnection);

               } else {
                  // check if already created or if endpoints are associated to different ports
                  if(currentEndpointA!=null && ! currentEndpointA.getId().equals(aObject.getId()) && ! currentEndpointA.getClassName().equals(aObject.getClassName() )) {
                     throw new IllegalArgumentException(" connection definition="+kuwaibaConnection+" link endpointA already associated with a differnt endpoint="+currentEndpointA.getId());
                  }
                  if(currentEndpointB!=null && ! currentEndpointB.getId().equals(bObject.getId()) && ! currentEndpointB.getClassName().equals(bObject.getClassName() )) {
                     throw new IllegalArgumentException(" connection definition="+kuwaibaConnection+" link endpointB already associated with a differnt endpoint="+currentEndpointB.getId());
                  }
                  if(LOG.isTraceEnabled()) LOG.trace("CONNECTION ALREADY EXISTS for connection definition=" + kuwaibaConnection);
               }

            } catch (Exception ex) {
               throw new IllegalArgumentException("problem associating containment objects for connection definition="+kuwaibaConnection, ex);
            }


         } else {

            // if no parent classes then create a simple physical container with unique name (i.e. a named cable)

            // check if container already exists
            try {
               // see if there is an object with the same name

               List<BusinessObject> foundObjects = bem.getObjectsWithFilter(connectionClassName, Constants.PROPERTY_NAME, name);
               if (!foundObjects.isEmpty()) {
                  BusinessObject createdObject = foundObjects.get(0);
                  if(LOG.isTraceEnabled()) LOG.trace("createConnections - CONNECTION OBJECT ALREADY EXIST " + businessObjectToString(createdObject));
                  kuwaibaClassesExisting++;
                  continue; // go to next connection
               }
            } catch (Exception ex) {
               throw new RuntimeException("problem finding existing container:", ex);
            }

            try {
               String aObjectId = aObject.getId();
               String bObjectId = bObject.getId();

               // find template if exists
               String templateId = null;
               if (connectionTemplateName != null && !connectionTemplateName.isEmpty()) {
                  List<TemplateObjectLight> foundTemplates = aem.getTemplatesForClass(connectionClassName);
               for (TemplateObjectLight tmplate : foundTemplates) {
                  if (connectionTemplateName.equals(tmplate.getName())) {
                     templateId = tmplate.getId();
                        if(LOG.isTraceEnabled()) LOG.trace("creating connection " + connectionClassName + " with connectionTemplateName: " + connectionTemplateName + " templateId: " + tmplate.getId());
                     break;
                  }
               }
            }
   
            String userName = "admin";

               if(LOG.isDebugEnabled()) LOG.debug("creating connection name=" + name + " connectionClass=" + connectionClassName + " template=" +
                     templateId + " to end objects aObject=" + businessObjectToString(aObject) + " bObject=" + businessObjectToString(bObject));

            physicalConnectionService.createPhysicalConnection(aObject.getClassName(), aObjectId, bObject.getClassName(), bObjectId, name, connectionClassName, templateId, userName);

         } catch (Exception ex) {
            throw new IllegalArgumentException("problem creating physical connection:", ex);
         }

      }

   }
}

   // TODO - allow service access from script in kuwaiba
   // this is a clone of methods in the internal PhysicalConnectionsService because the service is not accessible from a script
   @CompileStatic
   public static class PhysicalConnectionsServiceProxy  {
      
      private ApplicationEntityManager aem;

      private BusinessEntityManager bem;

      private MetadataEntityManager mem;

      public PhysicalConnectionsServiceProxy(ApplicationEntityManager aem, BusinessEntityManager bem, MetadataEntityManager mem) {
         super();
         this.aem = aem;
         this.bem = bem;
         this.mem = mem;
      }
      
      /**
       * A side in a physical connection.
       */
      public static String RELATIONSHIP_ENDPOINTA = "endpointA"; //NOI18N
      /**
       * B side in a physical connection.
       */
      public static String RELATIONSHIP_ENDPOINTB = "endpointB"; //NOI18N


      /**
       * Creates a physical connection.
       * @param aObjectClass The class name of the first object to related.
       * @param aObjectId The id of the first object to related.
       * @param bObjectClass The class name of the second object to related.
       * @param bObjectId The id of the first object to related.
       * @param name The connection name.
       * @param connectionClass The class name of the connection. Must be subclass of GenericPhysicalConnection.
       * @param templateId Template id to be used to create the current object. 
       * Use null as string or empty string to not use a template.
       * @param userName The user name of the session.
       * @return The id of the newly created physical connection.
       * @throws IllegalStateException
       * @throws OperationNotPermittedException
       */
      public String createPhysicalConnection(String aObjectClass, String aObjectId, 
          String bObjectClass, String bObjectId, String name, String connectionClass,
          String templateId, String userName) throws IllegalStateException, OperationNotPermittedException {
         
//          if (persistenceService.getState() == EXECUTION_STATE.STOPPED)
//              throw new IllegalStateException(ts.getTranslatedString("module.general.messages.cant-reach-backend"));
          
          String newConnectionId = null;
          try {
              if (!mem.isSubclassOf(Constants.CLASS_GENERICPHYSICALCONNECTION, connectionClass)) //NOI18N
                  throw new OperationNotPermittedException(connectionClass+" is not a subclass of "+ Constants.CLASS_GENERICPHYSICALCONNECTION); //NOI18N
              
              //The connection (either link or container, will be created in the closest common parent between the endpoints)
              BusinessObjectLight commonParent = bem.getCommonParent(aObjectClass, aObjectId, bObjectClass, bObjectId);
              
              if (commonParent == null || commonParent.getName().equals(Constants.DUMMY_ROOT))
                  throw new OperationNotPermittedException("no common parent for A and B side");
              
              boolean isLink = false;
              
              //Check if the endpoints are already connected, but only if the connection is a link (the endpoints are ports)
              if (mem.isSubclassOf(Constants.CLASS_GENERICPHYSICALLINK, connectionClass)) { //NOI18N
                  
                  if (!mem.isSubclassOf(Constants.CLASS_GENERICPORT, aObjectClass) || !mem.isSubclassOf(Constants.CLASS_GENERICPORT, bObjectClass)) //NOI18N
                      throw new OperationNotPermittedException(" a or b side ared not ports");
                  
                  if (!bem.getSpecialAttribute(aObjectClass, aObjectId, RELATIONSHIP_ENDPOINTA).isEmpty()) //NOI18N
                      
                      throw new OperationNotPermittedException("A endpoint  not connected :"+ bem.getObjectLight(aObjectClass, aObjectId));

                  if (!bem.getSpecialAttribute(bObjectClass, bObjectId, RELATIONSHIP_ENDPOINTB).isEmpty()) //NOI18N
                      throw new OperationNotPermittedException("B endpoint  not connected :"+  bem.getObjectLight(bObjectClass, bObjectId));
                  
                  isLink = true;
              }

              
              HashMap<String, String> attributes = new HashMap<>();
              if (name == null || name.isEmpty())
                  throw new OperationNotPermittedException(" name is empty");
              
              attributes.put(Constants.PROPERTY_NAME, name);
              
              newConnectionId = bem.createSpecialObject(connectionClass, commonParent.getClassName(), commonParent.getId(), attributes, templateId);
              
              if (isLink) { //Check connector mappings only if it's a link
                  aem.checkRelationshipByAttributeValueBusinessRules(connectionClass, newConnectionId, aObjectClass, aObjectId);
                  aem.checkRelationshipByAttributeValueBusinessRules(connectionClass, newConnectionId, bObjectClass, bObjectId);
              }
              
              bem.createSpecialRelationship(connectionClass, newConnectionId, aObjectClass, aObjectId, RELATIONSHIP_ENDPOINTA, true);
              bem.createSpecialRelationship(connectionClass, newConnectionId, bObjectClass, bObjectId, RELATIONSHIP_ENDPOINTB, true);
              
              aem.createGeneralActivityLogEntry(userName, 
                      ActivityLogEntry.ACTIVITY_TYPE_CREATE_INVENTORY_OBJECT, String.format("%s [%s] (%s)", name, connectionClass, newConnectionId));
              
              return newConnectionId;
          } catch (InventoryException e) {
              //If the new connection was successfully created, but there's a problem creating the relationships,
              //delete the connection and throw an exception
              if (newConnectionId != null) {
                  try {
                      bem.deleteObject(connectionClass, newConnectionId, true);
                  } catch (InventoryException ex) {
                  }
              }
              throw new OperationNotPermittedException(e.getMessage());
          }
      }

   }
      

   /*
    * CLASS DEFINITIONS
    * These classes could be in separate java classes if not in a groovy script
    */
   
   @CompileStatic
   public static class ContainerColour {

      /*
       *  // https://www.thefoa.org/tech/ColCodes.htm
       *  Inside the cable or inside each tube in a loose tube cable, individual fibers will be color coded for identification
       *  1  Blue,  2  Orange, 3  Green, 4  Brown ,5  Slate, 6  White, 7  Red, 8  Black, 9  Yellow, 10    Violet, 11    Rose, 12    Aqua
       */
      public static final List<String> orderedFibreColours = Arrays.asList("Blue", "Orange", "Green", "Brown", "Slate", "White", "Red",
               "Black", "Yellow", "Violet", "Rose", "Aqua");

      public static String getColourForStrand(int no) {
         if (no < 1 || no > orderedFibreColours.size()) {
            throw new IllegalArgumentException("strand size out of range: " + no);
         }
         return orderedFibreColours.get(no - 1);
      }

      /**
       * Used to find the fiber container colours for nested containers for a given circuit number 
       * @param circuitNo circuit 1 .. n where n max is 12*12*12*12 - 1
       * @param depth number of layers to include in result. Null includes all layers
       * @return returns 4 segment array of colours for each nested container corresponding to a given circuit number
       */
      public static List<String> getNestedContainerColourList(int circuitNo, Integer depth) {
         if (circuitNo < 1)
            throw new IllegalArgumentException("circuitNo must be greater than 0: " + circuitNo);
         if (depth !=null && (depth < 1 || depth > 5) )
            throw new IllegalArgumentException("deapth must be greater than 0 and less than 5 : " + depth);

         ArrayList<String> containerColourList = new ArrayList<String>();
         int radix = orderedFibreColours.size();
         
         String basen = Integer.toString(circuitNo-1,radix );
         // escape %1$4s as breaks in groovy
         String paddedbasen = String.format("%1\0446s", basen).replace(' ', '0');
         
         //System.out.println(circuitNo+" basen="+basen+" paddedbasen="+paddedbasen);
         
         for(int i=0; i<paddedbasen.length(); i++) {
            String s = paddedbasen.substring(i, i+1);
            Integer colorIndex = Integer.parseInt(s, radix);
            //System.out.println("colorIndex:"+colorIndex);
            String color = orderedFibreColours.get( colorIndex );
            //System.out.println("color:"+color);
            containerColourList.add(color);
         }
         
         if(depth !=null) {
            ArrayList<String> colList = new ArrayList<String>(containerColourList.subList(containerColourList.size() - depth, containerColourList.size()));
            containerColourList = colList;
         }
         
         return  containerColourList ;

      }


      public static int getStrandForColour(String colour) {
         int no = orderedFibreColours.indexOf(colour);
         if (no < 0)
            throw new IllegalArgumentException("unknown fibre colour: " + colour);
         return no + 1;
      }

   }

   @CompileStatic
   public static class KuwaibaClass {

      private String className = null;
      private String templateName = null;
      private String name = null;
      private Boolean special = false;

      // parent classes contains a hierarchy of classes to be searched in order to find parent.
      private List<KuwaibaClass> parentClasses = new ArrayList<KuwaibaClass>();
   
      private HashMap<String, String> attributes = new HashMap<String, String>();
   
      public KuwaibaClass() {
         super();
      }
   
      public String getClassName() {
         return className;
      }
   
      public void setClassName(String className) {
         this.className = className;
      }
   
      public String getTemplateName() {
         return templateName;
      }
   
      public void setTemplateName(String templateName) {
         this.templateName = templateName;
      }
   
      public String getName() {
         return name;
      }
   
      public void setName(String name) {
         this.name = name;
      }
   
      public List<KuwaibaClass> getParentClasses() {
         return parentClasses;
      }
   
      public void setParentClasses(List<KuwaibaClass> parentClasses) {
         this.parentClasses = parentClasses;
      }
   
      public HashMap<String, String> getAttributes() {
         return attributes;
      }
   
      public void setAttributes(HashMap<String, String> attributes) {
         this.attributes = attributes;
      }
   
      public Boolean getSpecial() {
         return special;
      }
   
      public void setSpecial(Boolean special) {
         this.special = special;
      }
   
      @Override
      public String toString() {
         return "KuwaibaClass [ name=" + name + ", className=" + className + ", templateName=" + templateName + ", special=" + special +
                  ", parentClasses=" + parentClasses + ", attributes=" + attributes + "]";
      }
   
   }
   
   @CompileStatic
   public static class KuwaibaProvisioningRequisition {
      
      private List<KuwaibaTemplateDefinition> kuwaibaTemplateList = new ArrayList<KuwaibaTemplateDefinition>();
   
      private List<KuwaibaClass> kuwaibaClassList = new ArrayList<KuwaibaClass>();
      
      private List<KuwaibaConnection> kuwaibaConnectionList = new ArrayList<KuwaibaConnection>();
      
      public KuwaibaProvisioningRequisition() {
         super();
      }
      
      public List<KuwaibaTemplateDefinition> getKuwaibaTemplateList() {
         return kuwaibaTemplateList;
      }
   
      public void setKuwaibaTemplateList(List<KuwaibaTemplateDefinition> kuwaibaTemplateList) {
         this.kuwaibaTemplateList = kuwaibaTemplateList;
      }
      
      public List<KuwaibaClass> getKuwaibaClassList() {
         return kuwaibaClassList;
      }
   
      public void setKuwaibaClassList(List<KuwaibaClass> kuwaibaClassList) {
         this.kuwaibaClassList = kuwaibaClassList;
      }
   
      public List<KuwaibaConnection> getKuwaibaConnectionList() {
         return kuwaibaConnectionList;
      }

      public void setKuwaibaConnectionList(List<KuwaibaConnection> kuwaibaConnectionList) {
         this.kuwaibaConnectionList = kuwaibaConnectionList;
      }

      @Override
      public String toString() {
         return "KuwaibaProvisioningRequisition [kuwaibaTemplateList=" + kuwaibaTemplateList + ", kuwaibaClassList=" + kuwaibaClassList +
                  ", kuwaibaConnectionList=" + kuwaibaConnectionList + "]";
      }

   }

   @CompileStatic
   public static class KuwaibaTemplateDefinition {
      
      private String templateName = null;
      
      private String templateElementName = null;
      
      private String className = null;
   
      private String templateFunction = null;
      
      private Boolean special = false;
      
      private List<KuwaibaTemplateDefinition> childKuwaibaTemplateDefinitions = new ArrayList<KuwaibaTemplateDefinition>();
      
      private HashMap<String,String> templateFunctionAttributes = new HashMap<String,String>();
   
      private HashMap<String, String> templateAttributes = new HashMap<String, String>();

      public KuwaibaTemplateDefinition() {
         super();
      }
   
      public String getTemplateName() {
         return templateName;
      }
   
      public void setTemplateName(String templateName) {
         this.templateName = templateName;
      }
   
      public String getTemplateElementName() {
         return templateElementName;
      }
   
      public void setTemplateElementName(String templateElementName) {
         this.templateElementName = templateElementName;
      }
   
      public List<KuwaibaTemplateDefinition> getChildKuwaibaTemplateDefinitions() {
         return childKuwaibaTemplateDefinitions;
      }
   
      public void setChildKuwaibaTemplateDefinitions(List<KuwaibaTemplateDefinition> childKuwaibaTemplateDefinitions) {
         this.childKuwaibaTemplateDefinitions = childKuwaibaTemplateDefinitions;
      }
   
      public String getClassName() {
         return className;
      }
   
      public void setClassName(String className) {
         this.className = className;
      }
   
      public String getTemplateFunction() {
         return templateFunction;
      }
   
      public void setTemplateFunction(String templateFunction) {
         this.templateFunction = templateFunction;
      }
   
      public HashMap<String, String> getTemplateFunctionAttributes() {
         return templateFunctionAttributes;
      }
   
      public void setTemplateFunctionAttributes(HashMap<String, String> templateFunctionAttributes) {
         this.templateFunctionAttributes = templateFunctionAttributes;
      }
      
      public Boolean getSpecial() {
         return special;
      }
   
      public void setSpecial(Boolean special) {
         this.special = special;
      }
   
      public HashMap<String, String> getTemplateAttributes() {
         return templateAttributes;
      }

      public void setTemplateAttributes(HashMap<String, String> templateAttributes) {
         this.templateAttributes = templateAttributes;
      }

      @Override
      public String toString() {
         return "KuwaibaTemplateDefinition [templateName=" + templateName + ", templateElementName=" + templateElementName +
                  ", className=" + className + ", templateFunction=" + templateFunction + ", special=" + special +
                  ", childKuwaibaTemplateDefinitions=" + childKuwaibaTemplateDefinitions +
                  ", templateFunctionAttributes=" + templateFunctionAttributes + ", templateAttributes=" + templateAttributes + "]";
      }

   }

   @CompileStatic
   public static class KuwaibaConnection {
      
      private KuwaibaClass connectionClass;
      
      private KuwaibaClass endpointA;
      
      private KuwaibaClass endpointB;

      public KuwaibaConnection() {
         super();
      }

      public KuwaibaClass getConnectionClass() {
         return connectionClass;
      }

      public void setConnectionClass(KuwaibaClass connectionClass) {
         this.connectionClass = connectionClass;
      }

      public KuwaibaClass getEndpointA() {
         return endpointA;
      }

      public void setEndpointA(KuwaibaClass endpointA) {
         this.endpointA = endpointA;
      }

      public KuwaibaClass getEndpointB() {
         return endpointB;
      }

      public void setEndpointB(KuwaibaClass endpointB) {
         this.endpointB = endpointB;
      }

      @Override
      public String toString() {
         return "KuwaibaConnection [connectionClass=" + connectionClass + ", endpointA=" + endpointA + ", endpointB=" + endpointB + "]";
      }
      
   }

}
