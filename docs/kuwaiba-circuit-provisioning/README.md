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
The model importing mechanism we use in this POC is written as a groovy script which runs as a `task` within a standard Kuwaiba release.

![alt text](./images/importingKuwaiba1.png  "Figure importingKuwaiba1.png")

## Steps to create and import a GPON model
In this Proof of concept, we are targeting a GPON model. 
This is essentially a tree of optical fibers fanning out from a central exchange through first and second level splitters (which we label as Aggregators and Cabinets), and fiber splice points on telephone poles.

To start the simulation navigate to the top of the docker compose project and type

```
docker compose --profile prod up -d
```
If this is the first time you have run the model, you will need to wait which the images are donloaded and / or built.
After a while you will be able to navigate to the following pages;

* Kuwaiba importer application at [http://localhost:8080/index.html](http://localhost:8080/index.html)
* A swagger UI is provided to allow you to easily the run ReST commands which generate the model [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
  
* Kuwaiba admin terminal is available at [http://localhost:8085/kuwaiba/](http://localhost:8085/kuwaiba/) (username:admin , password:kuwaiba)

The following steps can be used to generate and install a GPON configuration in the Kuwaiba system.

### 1. Select the geographic area you want to generate a model for.

A pre-imported model of Bitterne Park is provided which can be used to run the prediction without importing (following from step 2 below).

Alternatively this can be overridden by selecting a new area to import on OpenStreetMap.
* Navigate to [http://localhost:8080/map.html](http://localhost:8080/map.html)
* Select the area you want to process and then press `Import Area`

![alt text](./images/select-area-to-provision.png  "Figure select-area-to-provision.png")

### 2. Predict the infrastructure.

Use the imported OpenStreetMap data to generate predicted infrastructure and fiber ducting.

![alt text](./images/prediction-controller-provision.png  "Figure prediction-controller-provision.png")

The prediction can be visualised using the [QGIS desktop application](https://www.qgis.org/) connected to the Postgis database running on the docker compose project.

![alt text](./images/qgisconnection.jpg  "Figure qgisconnection.jpg")

An example qgis project is also provided which you can load into the qgis desktop application `kuwaiba-export-project.qgs`

This visualiation helps show where the model predicts the infrastructure should be.

![alt text](./images/qgis-visualisation.png  "Figure qgis-visualisation.png")


### 3. Add the parent locations to situate the model in kuwaiba
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
The Kuwaiba script could be extended to use a ReST call to generate and import the json model directly. 
However at this point, we are simply loading the file from a known location injected within the Kuwaiba container.
Having generated the Json, we need to copy and paste it into the file which will be imported.

`osm-to-kuwaiba/container-fs/kuwaiba/external-data/kuwaibaProvisioningRequisition-data.json`

![alt text](./images/eclipse-kuwaiba-provision.png  "Figure eclipse-kuwaiba-provision.png")
 
If you change this file, the docker compose project should make the changes available inside the container in orde to run the script.

### 6. Select and run the Kuwaiba Importing Script using the tasks dialogue.

Note that you can run the script without changing the database as a test of the data integrity, or you can select to commit the changes. 

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

![alt text](./images/kuwaibanavigation1.png  "Figure kuwaibanavigation1.png")

## Manually adding end to end GPON circuits over the generated fiber containers
Having imported the fiber containers and the templates for the Fiber Exchanges, cabinets, poles and fiber cables, we can now manually add end to end circuits on top of the model using the tools provided natively by Kuwaiba.
In order to connect a circuit end to end, you need to identify each segment and connect the optical terminals, splitters and splice points to the correct fiber in each cable segment. 
This does take a bit of time searching for the correct next hop, so it will be useful in future to generate all of these links at the same time as the underlying infrastructure.

Connecting the LTE in the fiber exchange to the first splitter (Aggregator)

![alt text](./images/editconnections1.png  "Figure editconnections1.png")

Conecting the Aggregator to the street cabinet

![alt text](./images/editconnections2.png  "Figure editconnections2.png")

Connectign the street cabinet to the pole

![alt text](./images/editconnections3.png  "Figure editconnections3.png")

Connecting the pole to the customer service point (CSP splice) in the home

![alt text](./images/editconnections4.png  "Figure editconnections4.png")

Connecting the CSP to the ONT terminal

![alt text](./images/editconnections5.png  "Figure editconnections5.png")

Once the path has been created, it is possible to visualise it from the LTE port right down to the house,

Select the port you want to visualise and then select the Path or Tree view to trace the gpon from the LTE to the OLD.

Path view

![alt text](./images/editconnections6-pathview.png  "Figure editconnections6-pathview.png")

Tree View

![alt text](./images/editconnections6-treeview.png  "Figure editconnections6-treeview.png")


## Generated Kuwaiba Templates

The following templates are created as part of the population. These tempaltes are used to simplify the provisioning requisition.

A template for a fiber exchange rack

![alt text](./images/KuwaibaTemplateFiberExchangeRack.png  "Figure KuwaibaTemplateFiberExchangeRack.png")

A template for a blown fiber containers

![alt text](./images/KuwaibaTemplateBFU4_12.png  "Figure KuwaibaTemplateBFU4_12.png")

A template for the OLT and Customer service point (splice) in the house

![alt text](./images/KuwaibaTemplateHouse1.png  "Figure KuwaibaTemplateHouse1.png")








