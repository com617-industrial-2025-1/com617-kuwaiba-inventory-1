# Importing data to Kuwaiba

## Core Kuwaiba concepts (Objects and Templates)
Kuwaiba is a telecoms inventory system which is able to model physical and logical infrastructure deloyed at city scales.
Central to Kuwaiba is the concept of an extensable physical and logical object model. 

![alt text](./images/KuwaibaInventoryObjects.png  "Figure KuwaibaInventoryObjects.png")

These can be assembled and specialised using templates which allow repeatable configurations of telecoms equipment to be applied in different parts of the network. 
In this proof of concept, we are using Open Streetmap data to generate a realistic example network with templates for fiber exchanges, street cabinates and fiber cables.

Kuwaiba also provides the ability to extend the internal code using groovy scripts. 
The model importing mechanism we use in this POC is written as a groovy script which runs with in a standard kuwaiba release.

![alt text](./images/importingKuwaiba1.png  "Figure importingKuwaiba1.png")

The following steps can be used to generate and install a configuration in the kuwaiba system.

## Select the area geographic you want to generate a model for

A pre-imported model of Bitterne Park is provided which can be used to run the prediction. 
However this can be overridden by selecting a new area to import.
Select the area then press `Import Area`

![alt text](./images/select-area-to-provision.png  "Figure select-area-to-provision.png")

## Use the imported Open Steet Map data generate predicted infrastructure and fiber ducting.

![alt text](./images/prediction-controller-provision.png  "Figure prediction-controller-provision.png")

The prediction can be visualised using the QGIS desktop applciation connected to the Postgis database

## Add the parent locations

![alt text](./images/set-parent-names-provision.png  "Figure set-parent-names-provision.png")

## Generate a Kuwaiba Requisition

![alt text](./images/export-circuits-provision.png  "Figure export-circuits-provision.png")

## Copy the generated json into a file which is injected into the Kuwaiba container

![alt text](./images/eclipse-kuwaiba-provision.png  "Figure eclipse-kuwaiba-provision.png")

## Select and run the Kuwaiba Importing Script

![alt text](./images/importingKuwaiba2.png  "Figure importingKuwaiba2.png")

## Check the script completed correctly
The script will take some time to complete as it is single threaded.
You should get a result page once the script completes. 
However if you are importing a large data file, the Kuwaiba session may time out and you will see an error result.
This does not necessarily mean that the script has failed.
Look at the logs to check the script completes

![alt text](./images/FinishImportingKuwaiba1.png  "Figure FinishImportingKuwaiba1.png")



# Manually adding end to end GPON circuits over the generated fiber containers

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

Templates for blown fiber containers

![alt text](./images/KuwaibaTemplateBFU4_12.png  "Figure KuwaibaTemplateBFU4_12.png")

A template for the OLT and Customer service point ( splice) in the house

![alt text](./images/KuwaibaTemplateHouse1.png  "Figure KuwaibaTemplateHouse1.png")








