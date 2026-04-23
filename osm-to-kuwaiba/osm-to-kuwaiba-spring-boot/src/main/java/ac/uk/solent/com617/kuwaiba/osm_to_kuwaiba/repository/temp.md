BuildingDropPointRepository
contains 2 SQL startments
1) insertPoleClusters(): this statement clusters buildings in chunks of 12 using K-Means clustering. Each cluster made has a geographic centre which snaps the cluster to the nearest road. This point is then saved into network_points.
2) updateBuildingParents(): after poles are placed, this statement assigns each building drop point to the nearest pole and uses a lateral join to find the closest pole using ST-distance

CleanedBuildingRepository
A boilerplate Spring Data repo for CleanedBuilding.

CleanedRoadRepository.java
plain JPA repo for CleanedRoad used to access cleaned geospatial data

LinkedBuildingRepository
Extends JpaRepository for LinkedBuilding
A query that joins OSM building polygons with the UPRN points. It cheks if a UPRN coordinate “fits” into the buildings geometry. Distinct is used to make sure only one UPRN is assigned per building 


NodedStreetsRepository
Placeholder for future use (possibly)
