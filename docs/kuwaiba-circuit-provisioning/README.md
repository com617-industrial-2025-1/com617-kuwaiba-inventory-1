# Importing data to Kuwaiba

## Core Kuwaiba concepts (Objects and Templates)
Kuwaiba is a telecoms inventory system which is able to model physical and logical infrastructure deloyed at city and country scales.
Central to Kuwaiba is the concept of an extensible physical and logical object model. 
An excerpt of the standard built-in model is shown below.

![alt text](./images/KuwaibaInventoryObjects.png  "Figure KuwaibaInventoryObjects.png")

These physical and logical model objects can be assembled and specialised using templates which allow repeatable configurations of telecoms equipment to be applied in different parts of the network. 
In this proof of concept, we are using OpenStreetMap data to generate a realistic example network using templates for fiber exchanges, street cabinets and fiber cables.
Some example templates are shown at the bottom of this page.

Kuwaiba also provides the ability to extend the internal code using groovy scripts. 
The model importing mechanism we use in this POC is written as a groovy script which runs as a `task`with in a standard Kuwaiba release.

![alt text](./images/importingKuwaiba1.png  "Figure importingKuwaiba1.png")

## Steps to create and import a GPON model
In this Proof of concept, we are targeting a GPON model. 
This is essentially a tree of optical fibers fanning out from a central exchange through first and second level splitters (which we label as Aggregators and Cabinets), and fiber splice points on telephone poles.

The following steps can be used to generate and install a GPON configuration in the Kuwaiba system.

### 1. Select the geographic area you want to generate a model for.

A pre-imported model of Bitterne Park is provided which can be used to run the prediction. 
However this can be overridden by selecting a new area to import.
Select the area then press `Import Area`

![alt text](./images/select-area-to-provision.png  "Figure select-area-to-provision.png")

### 2. Use the imported OpenStreetMap data to generate predicted infrastructure and fiber ducting.

![alt text](./images/prediction-controller-provision.png  "Figure prediction-controller-provision.png")

The prediction can be visualised using the QGIS desktop application connected to the Postgis database.
This visualiation helps show where the model predicts the infrastructure should be.

![alt text](./images/qgis-visualisation.png  "Figure qgis-visualisation.png")

### 3. Add the parent locations to situate the model
In order to export data to Kuwaiba, we need to add data which identifies where in the world this extracted map is located. 
This is characterised as Continent, Country, City, Neighborhood location.

![alt text](./images/set-parent-names-provision.png  "Figure set-parent-names-provision.png")

### 4. Generate a Kuwaiba Requisition.
The kuwaiba requisition file contains a json model which can be imported into Kuwaiba using the supplied script.
The model consists of templates, some static parent objects (country, areas, street names etc), objects which represent fixed infrastructure points 
(Exchanges, Cabinets Poles, Houses etc) and finally circuit definitions which define the fiber ducts and fibers between the fixed points. 
The generated model provides the base network on top of which individual customer circuits can be manually provisioned later.

![alt text](./images/export-circuits-provision.png  "Figure export-circuits-provision.png")

### 5. Copy the generated json into a file which is injected into the Kuwaiba container.
The script could be extened to use a ReST call to generate and import the json model directly. 
However at this point, we are simply loading the file from a known location injected within the Kuwaiba container.
Having generated the Json, we need to copy and paste it into the file which will be imported 

![alt text](./images/eclipse-kuwaiba-provision.png  "Figure eclipse-kuwaiba-provision.png")

### 6. Select and run the Kuwaiba Importing Script.

![alt text](./images/importingKuwaiba2.png  "Figure importingKuwaiba2.png")

### 7. Check the script completed correctly
The script will take some time to complete as it is single threaded.
You should get a result page once the script completes. 
However if you are importing a large data file, the Kuwaiba jetty session may time out and you will see an error result.
This does not necessarily mean that the script has failed.
Look at the logs to check the script completes

![alt text](./images/FinishImportingKuwaiba1.png  "Figure FinishImportingKuwaiba1.png")

We now have a complete network imported into the system.
You can navigate through the network to identify the components, in this case organised by streets generated under `Europe/United Kingdom/Southampton/BitternePark`


# Manually adding end to end GPON circuits over the generated fiber containers
Having imported the fiber containers and the templates for the Fiber Exchanges, cabinates, poles and fiber cables, we can now manually add end to end circuits on top of the model using the tools provided natively by Kuwaiba.

![alt text](./images/editconnections1.png  "Figure editconnections1.png")

![alt text](./images/editconnections2.png  "Figure editconnections2.png")

![alt text](./images/editconnections3.png  "Figure editconnections3.png")

![alt text](./images/editconnections4.png  "Figure editconnections4.png")

![alt text](./images/editconnections5.png  "Figure editconnections5.png")

![alt text](./images/editconnections6-pathview.png  "Figure editconnections6-pathview.png")

![alt text](./images/editconnections6-treeview.png  "Figure editconnections6-treeview.png")


## Generated Kuwaiba Templates

A template for a fiber exchange rack

![alt text](./images/KuwaibaTemplateFiberExchangeRack.png  "Figure KuwaibaTemplateFiberExchangeRack.png")

A template for a blown fiber containers

![alt text](./images/KuwaibaTemplateBFU4_12.png  "Figure KuwaibaTemplateBFU4_12.png")

A template for the OLT and Customer service point (splice) in the house

![alt text](./images/KuwaibaTemplateHouse1.png  "Figure KuwaibaTemplateHouse1.png")








