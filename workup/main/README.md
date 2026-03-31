# OSM to Kuwaiba

A Spring Boot application that processes OpenStreetMap data to predict fiber network infrastructure 
(placing poles, cabinets, aggregators, and exchanges) and exposes the results via a REST API.

## Prerequisites
Before running this project you will need the following installed:
- Docker Desktop
- Java 21+
- Maven
- Eclipse(Development)

## Project Structure

## Setup and Running
**Step 1 - Start the Docker Environment** 

The Docker environment handles setting up the database ,importing the OSM data, and starting a
pgAdmin container.
1. Starts a PostGIS database
2. Runs the OSM importer (osm2pgsql)

Make sure you are in the root directory `/main`
```bash
docker compose up
```

Wait until the `importer` container exits. Once it has finished the database is ready for the
Spring Boot Application.

To start fresh(wiping all data):
```bash
docker compose down -v
docker compose up
```

**Step 2 - Run the Spring Boot Application**

On startup the Spring Boot Application will automatically:
- Create the database schema
- Import the UPRN data
- Link UPRNs to buildings

From the command line:
```bash
# Make sure you are in the osm-to-kuwaiba project
cd osm-to-kuwaiba 

# Run directly with Maven (recommended for development)
mvn spring-boot:run

# Or skip tests for faster startup
mvn spring-boot:run -DskipTests

# Building a jar (will be done for production)
mvn package -DskipTests
java -jar target/osm-to-kuwaiba-0.0.1-SNAPSHOT.jar
```

In Eclipse:

right-click `OsmToKuwaibaApplication.java` -> Run As -> Spring Boot App

When you see the following statements in the console, the app is ready:
```
Tomcat started on port(s): 8080 (http)

UPRN IMPORT AND LINKING COMPLETE
```

## REST API
To use the REST API you can use a tool like [Postman](https://www.postman.com/downloads/) to send
POST and GET requests.

Alternatively Swagger UI has been implemented. Once the app is running you'll have two URLs 
available:

- `http://localhost:8080/swagger-ui.html` — the interactive UI where you can click endpoints and hit
  "Execute" to run them.
- `http://localhost:8080/v3/api-docs` — the raw JSON spec if you ever need it

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

The database runs in Docker on `localhost:5432`

|Setting|Value|
|-------|-----|
|Host|localhost|
|Port|5432|
|Database|osm|
|Username|osmuser|
|Password|osmpass|

pgAdmin is available at `http://localhost:8888` for browsing the database directly.

|Setting|Value|
|-------|-----|
|Email|user-name@domain-name.com|
|Password|minad1234|

## Running Tests
Tests require the Docker database to be running. Then from the project root:

```bash
cd main/osm-to-kuwaiba
mvn test
```

Individual test classes can be run from Eclipse by 
right-clicking the test file -> Run As -> JUnit Test

**Note**: Test uses `@Transactional` so all test data is automatically rolled back after each test, 
the live database is not affected.

## Developer Notes

I believe that an unintentional side effect of dropping tables using the sql script is that any
time the spring boot application is started any previous prediciton data is removed. This means
the lifetime of the data is the lifetime of the spring boot application running.

**UPRN PROBLEM** with the uprns matching up to houses that share the same building or are flats on 
top of eachother

### TODO
- [ ] Improve Prediction
  - [ ] Relevant Connections follow roads
  - [x] Points are placed next to roads
- [ ] QGIS in a docker container
- [ ] Export Mechanism
- [ ] Naming Conventions


