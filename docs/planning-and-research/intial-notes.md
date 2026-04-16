# Goals

## Extract streets and Houses from OSM 

* Overpass API or HOT Export Tool. https://overpass-turbo.eu/ https://export.hotosm.org/v3/

* Can query OSM for specific tags. 

* JOSM to download a specific area and filter, or osm2streets library it turns map data into a clean graph. 

## Predict Infrastructure
* FiberQ   https://www.fiberq.net/
* 
* FiberQ allows placement of poles and manholes on the streets 

## Export Data to Kuwaiba 

* Convert data into csv 

* Design fiber layout in QGIS using FiberQ 

* Export List of poles, cabinets, and cables as csv 

* Format data to match Kuwaiba’s data model 


## Potential Tech Stack

| Datasource | OpenStreetMap |
|:---------------|:----|
|Network Design|FibreQ|
|Simulation|?|
|Inventory|Kuwaiba|

## some classes to look at

get subset of ordinance survey uprn using geobounding

raw OS date from https://www.ordnancesurvey.co.uk/products/os-open-uprn  each uprn has lat and lon and UPRN number

https://github.com/gallenc/kuwaiba-examples-1/blob/main/workup/kuwaiba-opennms-integration/reporttester/src/test/java/org/entimoss/misc/test/ExtractFromUPRNGis.java

get address for given lat/lon date in Ordinance survey GIS (uses nominatum api)

https://github.com/gallenc/kuwaiba-examples-1/blob/main/workup/kuwaiba-opennms-integration/reporttester/src/test/java/org/entimoss/misc/test/ExtractOSMDataFromUprn.java

```
      // https://nominatim.openstreetmap.org/reverse?format=geojson&lat=50.9246111&lon=-1.3719191
      String url = "https://nominatim.openstreetmap.org/reverse?format=geojson"
               + "&lat=" + latitude
               + "&lon=" + longitude;
```
