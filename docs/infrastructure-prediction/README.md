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

### PGRouting

As PGRouting is an extension of the PostGIS database, all functionality is done in the form of
SQL queries. QGIS is only used for the visualisation aspect to check the work done.

After ideas for storing data in different tables was explored the following three tables should be
used:

**Physical Infrastructure**
This includes all the static elements that are in one place e.g. splitters and poles.

| id | asset_tag | type | longitude | latitude | max_ports | notes |
|---|---|---|---|---|---|---|
|1|SOU_EXCH_01|Exchange|-1.404212|50.906541|10000|Southampton Central Exchange|
|2|SOU_AGG_B1|Agg_Node|-1.385421|50.921345|48|Serving Bitterne Park Sector 1|
|3|SOU_CAB_B1_01|Cabinet|-1.384102|50.923110|30|Street Cabinet (Secondary Splitter)|
|4|SOU_POLE_001|Pole|-1.383550|50.923551|12|12-Port CBT Mounted|
|5|SOU_POLE_002|Pole|-1.383120|50.923980|8|8-Port CBT Mounted|

**Fiber Segments**
This contains data for the connections between the static elements.

We can also break up the types of connections into four types:
- Lines from distribution to aggregation nodes
- Lines from aggregation nodes to splitters
- Lines from splitters to poles
- Lines from poles to buildings

|id|segment_type|source_id|target_id|length_m|geom_wkt (Simplified)|
|---|---|---|---|---|---|
|101|Spine|1|2|1850.4|LINESTRING(-1.404... -1.385...)|
|102|Distribution|2|3|420.2|LINESTRING(-1.385... -1.384...)|
|103|Lateral|3|4|85.5|LINESTRING(-1.384... -1.383...)|
|104|Drop|4|5001|12.3|LINESTRING(-1.383... -1.3832...)|

`geom_wkt` is geometry well-known text. The LINESTRING data type provides a list of coordinates
along a road in this case. This can be handled by PGRouting and [pgr_dijkstra](https://apslight.github.io/doc/html/en/pgr_dijkstra.html)
LOOK INTO PGR_DIJKSTRA

**Service Connections**
A non spatial table that tracks which building is connected to each specific pole/cabinet.

|uprn|service_pole_id|splitter_cab_id|install_type|
|---|---|---|---|
|10001234567|4|3|Aerial|
|10001234568|4|3|Aerial|
|10001234569|5|3|Underground|
|20000455121|NULL|3|Direct (MDU)|



### Tools

#### [QGIS](https://qgis.org/)
A Spatial Visualisation and decision making tool.

#### [FiberQ](https://www.fiberq.net/) (QGIS Plugin)

#### [PGRouting](https://qgis.pgrouting.org/) 


