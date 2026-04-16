# Data Gathering and Import Documentation


Initial Notes:

Documentation on where the OSM data can be gathered from and what the osm2pgsql tool does to the 
data.

Also what the process for cleaning and processing the data does.

Note: There is some overlap into repositories and models

Relevant Files and Info:
- [docker-compose.yml](../../osm-to-kuwaiba/docker-compose.yml)
- For data cleaning and processing:
    - [createtables.sql](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/resources/createtables.sql)
    - [BuildingDropPointRepository](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/java/ac/uk/solent/com617/kuwaiba/osm_to_kuwaiba/repository/BuildingDropPointRepository.java)

