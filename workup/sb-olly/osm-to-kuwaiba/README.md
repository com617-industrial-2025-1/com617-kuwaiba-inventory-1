# OSM to Kuwaiba

A Spring Boot application that processes OpenStreetMap data to predict fiber network infrastructure 
(placing poles, cabinets, aggregators, and exchanges) and exposes the results via a REST API.

## Prerequisites
Before running this project you will need the following installed:
- Docker Desktop
- Java 21+
- Maven

## Project Structure

## Setup and Running
**Step 1 - Start the Docker Environment** 

The Docker project handles all data preparation automatically in sequence:
1. Starts a PostGIS database
2. Runs the OSM importer (osm2pgsql)
3. Creates and Populates the required tables

```bash
cd data-processing
docker compose up
```

Wait until all containers finish. The `table-creator` container exiting with code 0 means everything
is ready.

To start fresh(wiping all data):
```bash
docker compose down -v
docker compose up
```

**Step 2 - Run the Spring Boot Application**

In Eclipse:

right-click `OsmToKuwaibaApplication.java` -> Run As -> Spring Boot App

Or from the command line:
```bash
cd osm-to-kuwaiba
./mvnw spring-boot:run
```

When you see the following in the console the app is ready:
```
Tomcat started on port(s): 8080 (http)
```

## REST API
**Prediction Endpoints**

These trigger the network prediction algorithms. These should be run first to populate the network 
tables.

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
./mvnw test
```

Individual test classes can be run from Eclipse by 
right-clicking the test file -> Run As -> JUnit Test

**Note**: Test uses `@Transactional` so all test data is automatically rolled back after each test, 
the live database is not affected.




