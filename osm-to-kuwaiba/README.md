# OSM-To-Kuwaiba

A Spring Boot Application that processes Open Street Map (OSM) data to predict a fibre network.

Poles, Cabinets, Aggregators, Exchanges are placed and results exposed via a REST API.

The prediction uses PostGIS spatial clustering to cluster buildings and place network points.
pgRouting is used to trace road connections between these points and make connections.

Results can be queried as GeoJSON for use in GIS tools as well as exported for Kuwaiba.

### Limitations


## Prerequisites
- Docker Desktop

The entire stack runs inside docker. Java and Maven are needed if the intention is to run the
application outside of docker.

## Using your own data
1. OSM exports can be gathered for an area of interest in `.xml` format online. 
2. The `.xml` file should be placed in `container-fs/data/` replacing existing `.xml` files.
   **Only one `.xml` file should be present at a time.**
3. UPRN CSV data can be gathered from the area and placed in `container-fs/data/` named `uprns.csv`
   replacing any existing files.

**Note**: All buildings present in the OSM file are included in the prediction regardless if they
have a UPRN or not. UPRNS are used to enrich the output with official address references.

## Running the Application

```bash
docker compose up -d
```

This will:
1. Start the PostGIS/pgRouting database
2. Run the OSM importer to load the XML data
3. Build and start the Spring Boot application once the import is complete

The first run may take several minutes because docker must download all images and maven must
download all dependencies. Subsequent runs are much faster as cached layers are used.

### Using the REST API
The simplest way this can be done is through the Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```
The endpoints available are as follows:

**Prediction Endpoints**

These trigger the network prediction algorithms. These should be run after the application has
started.

|Method|Endpoint|Description|
|------|--------|-----------|
|POST|`/predict/all`|Run full clustering and routing pipeline|
|POST|`/predict/clustering`|Run clustering only|
|POST|`/predict/routing`|Run routing only|

**Query Endpoints**

These return the predicted network data.

|Method|Endpoint|Description|
|------|--------|-----------|
|GET|`network/points`|Get all network points|
|GET|`/network/points/type?type=POLE`|Get points by type|
|GET|`/network/connections`|Get all network connections|
|GET|`/network/connections/type?linkType=DROP`|Get connections by type|

**Point Types**: `POLE`, `CABINET`, `AGGREGATOR`, `EXCHANGE`
**Connection Types**: `DROP`, `FEEDER`, `DISTRIBUTION`, `TRUNK`

## Database

The database runs in Docker on `localhost:5431`

|Setting|Value|
|-------|-----|
|Host|localhost|
|Port|5431|
|Database|osm|
|Username|osmuser|
|Password|osmpass|

pgAdmin is available at `http://localhost:8887` for browsing the database directly.

|Setting|Value|
|-------|-----|
|Email|user-name@domain-name.com|
|Password|minad1233|