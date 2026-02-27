# Contents

[Street Fibre](../streat-fibre.md)

### Rules for Infastructure Prediction

We should develop a list of requirements and rules for the prediction algorithm. Information
gathered on this can be found at the following sources:

[Think Broadband](https://www.thinkbroadband.com/news/4440-openreach-gearing-up-for-fttp) provides
- "Usually five or six properties (maximum 12) connected to one Manifold, which will be underground 
  and no further than 60m from the properties."
- "Each manifold links back to a larger splitter node via fibre. Each splitter note supporting 32 
  manifolds."
- "Each splitter links back to an aggregation point, where multiple splitter links arrive."
- "The aggregation point uses another larger fibre to link back to the Next Generation Access 
  Handover node."
- **Conclusion**
  - The point of connection from building to the manifold or CBT should be no further than 60m
  - Splitters support up to 32 manifolds which each support up to 12 buildings. 
  - This network should branch back to a "access handover point".

[Think Broadband 2](https://www.thinkbroadband.com/guides/fibre-fttc-ftth-broadband-guide) shows the
difference between Fibre to the Cabinet (FTTC) and Fibre to the Home/Premises (FTTH/FTTP).
- FTTC runs fibre from the telephone exchange to street cabinets where it is then connected to
  copper phone lines to distribute broadband.
- FTTH/FTTP provides an end to end fibre optic connection this invovles manhole aggregation and
  splitter nodes.
- **Conclusion**
  - This could be an overcomplication for our project but outlines the different distribution
    methods of fibre. 

[Prysmian](https://uk.prysmian.com/media/news/what-is-a-connectorised-block-terminal) shows the use
of Connectorised Block Terminals (CBT). 
- "The CBT is connected back to the exchange via fibre-optic cable and must be installed in an 
  underground chamber or attached to a telegraph pole close to the premises where FTTP is being 
  installed."
- "CBT also comes in three sizes with 4,8 and 12 ports".
- **Conclusion**
  - From telegraph poles with CBT's a maximum of 12 buildings can be connected.

[BT Community](https://community.bt.com/t5/BT-Fibre-broadband/FTTP-What-the-maximum-cable-run-from-the-pole-to-house-and/td-p/2204005)
An expert posted on the forums that "The maximum span allowed between the pole and your house is 
68m"
- **Conclusion**
  - The length of cable from pole to house should not exceed 68m.

[Openreach](https://www.openreach.com/content/dam/openreach/openreach-dam-files/images/fibre-broadband/fibre-for-developers/guides-and-handbooks/oct-2019-update/Quick%20guide%20Joint%20boxes,%20footways%20and%20frames%20&%20covers%20V2%20web.pdf)
fibre cannot be bent but with the use of joint boxes it can.
- **Conclusion**
  - If the fibre has to turn more than 45 degrees then a joint box should be placed.

[UnionFiber](https://www.weunionfiber.com/optical-splitters-a-deep-dive-into-split-ratios-and-splitting-architectures-for-ftth-pon-network/)
shows two splitting architectures:
- Centralized Splitting Architecture: works as a point to multipoint star topology. Ideas for urban
  and suburban areas.
- Cascading Splitting Architecture: Smaller splitters (1:4, 1:8) split to eachother to get a total
  split ratio (1:4 to 1:8 creates 1:32). Ideal for rural areas.

At the present moment it is also under assumption that:
- Fibre should not be present on any motorways at all.
- Fibre can use A roads and B roads as fibre is usually present on footpaths or the road verge.
- Research needs to be done on intersection behaviour e.g. Fibre should never cross a multi lane 
  road at an angle.

#### Requirements
- The network should avoid motorways.
- Joint Boxes should be placed where fibre has to turn more than 45 degrees.
- Telegraph poles can be connected to 12 buildings max.
- The cable from telegraph poles to buildings can be no longer than 68m.
- Splitters support maximum 32 buildings with recommended at 30.
- Splitters can be linked to aggregation points.
- The network should begin at a telecomunications distribution point (Exchange).
- Use Centralized Splitting Architecture (Maybe Cascading for rural areas).

### Sample Data
There are no readily available PostGIS databases for testing on so a quick creation has to be done
using [osm2pgsql](https://osm2pgsql.org/).

For a test a slice of southampton was downloaded using [slice](https://slice.openstreetmap.us)

#### PostGIS Database

[PostgreSQL](https://www.postgresql.org/) can be installed along with PGAdmin4 through the
PostgreSQL installer.

Once the database was created the postgis extension was installed with the query:
```sql
    CREATE EXTENSION postgis;
    CREATE EXTENSION hstore;
```
The `hstore` extension allows us to pack an unlimited amount of data into a single column. This is
done because there are lots of unique specific tags in open street map data e.g. `material=wood`,
`colour=green`. This data will be kept in a column called `Tags`.

The following command was run to input the .pbf data into the database using `osm2pgsql` (note that
the path and localhost port are specific to my machine):
```
osm2pgsql -d Fiber_Test -U postgres -H localhost -P 5433 -W --slim --hstore --multi-geometry -S ""C:\prog\industrial-consulting-project\osm2pgsql\osm2pgsql-bin\default.style"" southampton.pbf
```

The following is the database visualised using QGIS:

![Test Database Visualisation QGIS](./images/southampton_test_database_visual_qgis.png)

**Coordinate Reference System**
This is the mathematical formula used to flatten the curve of the earth onto a 2D map.

Open Street Map uses EPSG:4326 with units in degrees (lat/long).
The British National Grid uses EPSG:27700 which has units in meters.

This poses a problem as we want the output to be a csv with coordinates (lat/long) but predicting
the infastructure in this manner will distort the length of cables in meters. A solution could be to
do the mathematical prediction using the EPSG:27700 system and switch to ESPG:4326 for the export of
the data at the end.

### Database Design

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

### Planning Java

#### File Structure
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


#### Workflow
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


#### Imports

Below are the libraries that are to be imported into the project:

- [PostgreSQL JDBC](https://jdbc.postgresql.org/): This allows our java program to connect to the
  database.
- [JTS Core](https://mvnrepository.com/artifact/org.locationtech.jts/jts-core): Provides point and
  LineString functionality for our java models so calculation can be done.
- [SLF4J Simple Provider](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple)
- [JUnit](https://junit.org/): Functionality for testing.
- [HikariCP](https://github.com/brettwooldridge/HikariCP): Database connection pool.


### Tools

#### [QGIS](https://qgis.org/)
A Spatial Visualisation and decision making tool.

#### [FiberQ](https://www.fiberq.net/) (QGIS Plugin)

#### [PGRouting](https://qgis.pgrouting.org/) 



