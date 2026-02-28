# Database and Program Design

## Database Design

The data recieved from OSM is split into four big tables:
- `point`: Addresses, Postboxe, Traffic lights, etc.
- `line`: Roads, footpaths, railways
- `polygon`: Building footprints, parks lakes, city boundaries.
- `roads`: Lightweight version of the line table.

**Naming Conventions for Infrastructure**
 Exchange: `EX-[ID]`
 Aggregator: `AGG-[ID]`
 Cabinet: `CAB-[ID]`
 Pole: `NAP-[ID]`
 Link: `LNK-[Start]-[End]`

Our idea is to make a table for each of the static infrastructure as well as the fiber connections.

**Buildings**
|uprn (int)|geom (point)|parent_pole_id (int) FK|
|---|---|---|
|100012345678|POINT(-1.40225 50.90625)|100|
|100012345679|POINT(-1.40226 50.90628)|100|

**Poles**
|id (int)|external_id (text)|parent_id (int) FK|geom (point)|
|---|---|---|---|
|100|NAP-SOU-01-001|10 (CAB-SOU-001-01)|POINT(-1.4022 50.9062)|
|101|NAP-SOU-01-002|10 (CAB-SOU-001-01)|POINT(-1.4024 50.9065)|

**Cabinets**
|id (int)|external_id (text)|parent_id (int) FK|geom (point)|
|---|---|---|---|
|10|CAB-SOU-001-01|1 (AGG-SOU-001)|POINT(-1.4020 50.9060)|
|11|CAB-SOU-001-02|1 (AGG-SOU-001)|POINT(-1.4025 50.9075)|

**Aggregators**
|id (int)|external_id (text)|parent_id (int) FK|geom (point)|
|---|---|---|---|
|1|AGG-SOU-001||POINT(-1.4012 50.9055)|
|2|AGG-SOU-002||POINT(-1.3980 50.9120)|

**Exchanges**
|id (int)|external_id (text)|geom (point)|
|---|---|---|
||||

**FiberConnections**
|id (int)|external_id (text)|start_id (int) FK|end_id (int) FK|link_type (text)|geom (LineString)|
|---|---|---|---|---|---|
|500|LNK-NAP-001_UPRN123|NAP-SOU-01-001|1000123456|DROP|LINESTRING(...)|

**[Spatial Indexing](https://postgis.net/workshops/postgis-intro/indexing.html)**
can be used to speed up

## Java Plan

### File Structure
The java file structure for the project can be seen below:

```
src/main/java/com/fiber/project/
├── Main.java                
├── config/
│   └── DbConfig.java      
├── dao/ (Data Access Objects)
│   ├── RoadNetworkDao.java  
│   └── InfrastructureDao.java 
├── logic/
│   ├── RoutingLogic.java
│   └── ClusteringLogic.java 
├── model/
│   ├── Exchange.java
│   ├── Aggregator.java  
│   ├── Cabinet.java 
│   ├── Pole.java         
│   ├── Building.java 
│   └── FibreLink.java   
└── util/
    └── GeometryMapper.java
       
```
The reasoning for doing it this way is that it follows the [DAO design pattern](https://www.geeksforgeeks.org/system-design/data-access-object-pattern/).
 
 **DAO Design Pattern**:
 - `dao/` The files here interact directly with the database running sql queries.
 - `logic/` The files here contain the mathematical logic for predicting the infrastructure.
 - `model/` These are relevant objects for the dao and logic files to interact with.
 - `config/` The `DbConfig` file contains DB credentials and Connection Pooling for DB.
 - `util/` The `GeometryMapper` file runs logic for the LineString operations.


### Workflow
For the prediction we can plan the static infrastructure from the bottom up and then form the
connections top down. First we have to estimate/predict where the exchange would be.

- **Static Element Prediction (Bottom Up)**
  - **Building to Pole**
    - Cluster nearby building together in groups of 12. (as per the requirements)
    - For each cluster we calculate the geographic center.
    - Then we find the smallest distance to a residential road that the point is. (This is the 
      location for a telegraph pole)
  - **Pole to Cabinet**
    - Then we cluster the telegraph poles into groups that contain maximum 30 houses. 
      (as per the requirements)
    - For each of these clusters we calculate the geographic center.
    - We find the shortest distance to a road that the point is. (This is the location for a 
      cabinet)
  - **Cabinet to Aggregator**
    - We can then cluster 48 splitters into a group. (as per the requirements)
    - For each cluster we calculuate the geographic center.
    - We can then snap this to the nearest A road or B road. (This is the Aggregator)
  - **Aggregator to Exchange**
    - We can take the geographic center of all the aggregators which can become the Exchange
- **Connection Prediction (Top Down)**
  - A new table should be created for the connections: `Distribution Links`.
  - **Exchange to Aggregator**
    - For each aggregator we run the routing query back to the exchange.
    - This should ideally prioritise main roads.
  - **Aggregator to Cabinet**
    - For each cabinet we run the routing query back to its aggregator.
    - This should use all roads available.
  - **Cabinet to Pole**
    - For each pole we run the routing query back to its cabinet.
  - **Pole to Building**
    - We run a straight line from each building back to its pole.
    - Any connection here that exceeds the 68m requirement should be dropped and a potential new
      pole should be put in close to the building.

### Imported libraries

Below are the libraries that are to be imported for the java:

- [PostgreSQL JDBC](https://jdbc.postgresql.org/): This allows our java program to connect to the
  database.
- [JTS Core](https://mvnrepository.com/artifact/org.locationtech.jts/jts-core): Provides point and
  LineString functionality for our java models so calculation can be done.
- [SLF4J Simple Provider](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple)
- [JUnit](https://junit.org/): Functionality for testing.
- [HikariCP](https://github.com/brettwooldridge/HikariCP): Database connection pool.