# Data Gathering and Import Documentation

## 1. Data Sourcing
OpenStreetMap data can be gathered from several sources:
* **osm2street:** The current dataset for this project was gathered and prepared using `osm2street`, which specializes in converting OSM data into highly detailed, street-centric geometries suitable for engineering tasks.
* **Geofabrik:** For larger-scale deployments (e.g., entire cities or regions), `Geofabrik` is the primary source for daily updated `.osm.pbf` extracts.

For this project, the raw data (such as `bittern_park.xml`) should be placed in the `container-fs/data/` directory.

## 2. Data Import (`osm2pgsql`)
The **importer** service in the `docker-compose.yml` file utilizes the `iboates/osm2pgsql` tool to convert raw XML data into a structured PostGIS database.

### What `osm2pgsql` does to the data:
* **Schema Creation:** It creates a series of tables (such as `planet_osm_polygon` and `planet_osm_line`) representing map features.
* **Geometry Conversion:** It converts the raw XML nodes and ways into spatial geometries (Points, LineStrings, and Polygons) using the **Web Mercator (SRID 3857)** projection.
* **Hstore Tags:** The importer uses the `--hstore` flag, which packs all additional OSM metadata (like building levels or street names) into a single "tags" column for flexible querying.
* **Slim Mode:** Using the `--slim` flag allows the tool to handle larger datasets by using the database as a temporary storage area during the import process.

## 3. Data Cleaning and Processing
Once the raw data is in the database, the `createtables.sql` script and `BuildingDropPointRepository.java` perform a multi-stage cleaning and enrichment process.

### Geometry Repair and Standardization
* **Building Repair:** Raw OSM polygons are repaired using `ST_MakeValid` and converted to `MultiPolygon` format to ensure they are compatible with spatial analysis.
* **Road Simplification:** Roads are simplified using `ST_SimplifyPreserveTopology` with a 0.5-meter tolerance to reduce complexity while maintaining connectivity.

### Topology and Attribute Fixing
* **Island Removal:** The script identifies "road islands"—segments of road that are not connected to any other segment at their start or end points—and removes them to prevent routing errors in the network.
* **Street Name Inference:** Buildings missing a street name are updated by finding the nearest road that *does* have a name using the PostGIS distance operator (`<->`).


### Network Infrastructure Generation
The system generates a logical "Drop Point" for every building, which serves as the physical connection point for fiber.
* **Drop Point Placement:** Points are created on the building's exterior ring at the location closest to the nearest road segment.
* **Pole Clustering:** The `BuildingDropPointRepository` uses the `ST_ClusterKMeans` algorithm to group drop points into clusters of 12. 
* **Infrastructure Placement:** A "POLE" is automatically inserted into the `network_points` table at the road-snapped centroid of each cluster to act as a distribution hub.


## 4. Repository and Model Overlap
There is a close relationship between the database tables and the Spring Boot application:
* **Data Models:** Tables like `cleaned_buildings` and `cleaned_roads` map directly to JPA entities (models) such as `CleanedBuilding.java` and `CleanedRoad.java`.
* **Repositories:** Logic that is too complex for standard Java code—such as the K-means clustering—is handled via "Native Queries" within repositories like `BuildingDropPointRepository.java`. This allows the application to leverage the high-performance spatial processing of the database while maintaining a clean Java interface.
