**Goals**

**Extract streets and Houses from OSM **
-Overpass API or HOT Export Tool. https://overpass-turbo.eu/ https://export.hotosm.org/v3/
-Can query OSM for specific tags. 
-JOSM to download a specific area and filter, or osm2streets library it turns map data into a clean graph. 

**Predict Infrastructure **
-FiberQ   https://www.fiberq.net/
-FiberQ allows placement of poles and manholes on the streets 

**Export Data to Kuwaiba **
-Convert data into csv 
-Design fiber layout in QGIS using FiberQ 
-Export List of poles, cabinets, and cables as csv 
-Format data to match Kuwaiba’s data model 


**Potential Tech Stack **

Data Source - OpenSteetMap 

Network Design - FiberQ 
Simulation - ?
Inventory  - Kuwaiba 
