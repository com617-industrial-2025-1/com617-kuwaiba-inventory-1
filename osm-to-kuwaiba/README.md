# OSM-To-Kuwaiba

A Spring Boot Application that processes Open Street Map (OSM) data to predict a fibre network.

Poles, Cabinets, Aggregators, Exchanges are placed and results exposed via a REST API.

The prediction uses PostGIS spatial clustering to cluster buildings and place network points.
pgRouting is used to trace road connections between these points and make connections.

Results can be queried as GeoJSON for use in GIS tools as well as exported for Kuwaiba.

## Prerequisites
- Docker Desktop

The entire stack runs inside docker. Java and Maven are needed if the intention is to run the
application outside of docker.

## Using your own data
1. OSM exports can be gathered for an area of interest in `.xml` format online. The recommended
   tools are:
   - [HOT Export Tool](https://export.hotosm.org/)
   - [Geofabrik](https://download.geofabrik.de/)
2. The `.xml` file should be placed in `container-fs/data/` replacing existing `.xml` files.
   **Only one `.xml` file should be present at a time.**
3. UPRN CSV data can be gathered from the area and placed in `container-fs/data/` named `uprns.csv`
   replacing any existing files. UPRN data can be gathered for your area from the
   [Ordnance Survey Open UPRN dataset.]

**Note**: UPRNs are optional. All buildings present in the OSM file are included in the prediction
regardless of whether they have a matching UPRN. UPRNs enrich the output with official address
references but are not required for the pipeline to run.

## Running the Application

**Start the stack:**
```bash
docker compose up -d
```

This will:
1. Start the PostGIS/pgRouting database
2. Run the OSM importer to load the XML data
3. Build and start the Spring Boot application once the import is complete

The first run may take several minutes because docker must download all images and maven must
download all dependencies. Subsequent runs are much faster as cached layers are used.

**Monitor the application startup:**
```bash
docker compose logs -f app
```

The application is ready when you see:
```
Tomcat started on port(s): 8080 (http)
```

## Running a Prediction

Once the application is ready, trigger the prediction through the Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```
Call `POST /predict/all` to run the full pipeline. This places all infrastructure points and then
traces all cable connections.

## REST API

Full interactive documentation is at `http:localhost:8080/swagger-ui/index.html`

### Prediction Endpoints

|Method|Endpoint|Description|
|------|--------|-----------|
|POST|`/predict/all`|Run full clustering and routing pipeline|
|POST|`/predict/clustering`|Run clustering only|
|POST|`/predict/routing`|Run routing only|

### Network Query Endpoints

|Method|Endpoint|Description|
|------|--------|-----------|
|GET|`network/points`|Get all network points|
|GET|`/network/points/type?type=POLE`|Get points by type|
|GET|`/network/connections`|Get all network connections|
|GET|`/network/connections/type?linkType=DROP`|Get connections by type|

### GeoJSON Endpoints

|Method|Endpoint|Description|
|------|--------|-----------|
|GET|`/kuwaiba-network/points`|All points as a GeoJSON FeatureCollection|
|GET|`/kuwaiba-network/connections`|All connections as a GeoJSON FeatureCollection|
|GET|`/kuwaiba-network/kuwaibaRequisition`|Kuwaiba ready GeoJSON FeatureCollection|

**Point Types**: `POLE`, `CABINET`, `AGGREGATOR`, `EXCHANGE`
**Connection Types**: `DROP`, `FEEDER`, `DISTRIBUTION`, `TRUNK`

## Database Access

pgAdmin is available at `http://localhost:8888` for browsing the database directly.

|Setting|Value|
|-------|-----|
|Email|user-name@domain-name.com|
|Password|minad1234|

The database is reachable directly on `localhost:5431`

|Setting|Value|
|-------|-----|
|Host|localhost|
|Port|5431|
|Database|osm|
|Username|osmuser|
|Password|osmpass|

## Resetting

To wipe all data and start fresh (e.g. after changing the input XML):

```bash
docker compose down -v
docker compose up -d
```
The `-v` flag removes the Docker volumes so the database is recreated from scratch on the next run.