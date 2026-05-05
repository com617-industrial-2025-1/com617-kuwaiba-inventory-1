# osm-to-kuwaiba-spring-boot

The Spring Boot REST API component of the OSM to Kuwaiba project. In normal usage this runs
as a Docker container: see the [parent README](../README.md). This file covers running and
testing the application outside of Docker for development purposes.

## Prerequisites

- Java 21+
- Maven
- The Docker database must be running (see below)

## Running Outside Docker

The application requires the PostGIS database to be available. Start only the database and
importer services from the `osm-to-kuwaiba` directory:

```bash
cd osm-to-kuwaiba
docker compose up -d db importer
```

Wait until the `importer` container exits (the OSM data must be loaded before the app starts).
You can check with:

```bash
docker compose logs importer
```

Then run the Spring Boot application from this directory:

```bash
./mvnw spring-boot:run
```

Or from Eclipse: right-click `OsmToKuwaibaApplication.java` → Run As → Spring Boot App.

The application is ready when the console shows:

```
Tomcat started on port(s): 8080 (http)
```

On startup the application automatically:
- Creates the database schema (tables and indexes)
- Imports the UPRN CSV into `raw_uprns`
- Links UPRNs to building footprints

## Running Tests

Tests require the database to be running. With `db` started as above:

```bash
./mvnw test
```

Individual test classes can be run from Eclipse by right-clicking the test file →
Run As → JUnit Test.

All tests use `@Transactional` so test data is rolled back automatically after each test —
the live database is not affected.

## Project Structure

```
osm-to-kuwaiba-spring-boot/
├── src/main/java/ac/uk/solent/com617/kuwaiba/osm_to_kuwaiba/
│   ├── config/         # Custom Jackson geometry serialisers (GeoJSON output)
│   ├── init/           # Startup initialisation (schema creation, UPRN import)
│   ├── models/         # JPA entity classes and enums
│   ├── repository/     # Spring Data JPA repositories (native PostGIS SQL queries)
│   ├── rest/           # REST controllers (prediction triggers, network queries)
│   └── services/       # Service layer (orchestration of repository calls)
└── src/test/           # Integration tests
```

## Key Dependencies

| Dependency | Purpose |
|---|---|
| Spring Boot 3 | Application framework |
| Spring Data JPA | Repository layer and database connectivity |
| Hibernate Spatial | Mapping PostGIS geometry columns to JTS types |
| JTS (LocationTech) | Java geometry objects |
| springdoc-openapi | Swagger UI (`/swagger-ui/index.html`) |
| PostgreSQL JDBC | Database driver |
