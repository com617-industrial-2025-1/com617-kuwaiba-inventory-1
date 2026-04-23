# Repositories and SQL queries Documentation
Relevant files are in the [repository folder](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/java/ac/uk/solent/com617/kuwaiba/osm_to_kuwaiba/repository/)
Initial Notes:

Documenting what the purpose of the Repository layer is for and the native SQL queries that are inside them (what are their purpose etc.)

BuildingDropPointRepository contains 2 SQL startments
insertPoleClusters(): this statement clusters buildings in chunks of 12 using K-Means clustering. Each cluster made has a geographic centre which snaps the cluster to the nearest road. This point is then saved into network_points.
updateBuildingParents(): after poles are placed, this statement assigns each building drop point to the nearest pole and uses a lateral join to find the closest pole using ST-distance

CleanedBuildingRepository A boilerplate Spring Data repo for CleanedBuilding.

CleanedRoadRepository.java plain JPA repo for CleanedRoad used to access cleaned geospatial data

LinkedBuildingRepository Extends JpaRepository for LinkedBuilding A query that joins OSM building polygons with the UPRN points. It cheks if a UPRN coordinate “fits” into the buildings geometry. Distinct is used to make sure only one UPRN is assigned per building

NodedStreetsRepository Placeholder for future use
