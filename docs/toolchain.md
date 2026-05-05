# Toolchain Documentation <!-- omit in toc -->
**Project**: OSM to Kuwaiba

## Table of Contents <!-- omit in toc -->
- [Overview](#overview)
- [Development Environment](#development-environment)
- [Version Control](#version-control)
- [Data Ingestion - Open Street Map](#data-ingestion---open-street-map)
  - [Ingestion Workflow](#ingestion-workflow)
- [Database - PostGIS](#database---postgis)
  - [Spatial Reference System](#spatial-reference-system)
  - [Database Objects / Tables](#database-objects--tables)
  - [Database Connection (via Docker)](#database-connection-via-docker)
- [Prediction Engine - Java \& SQL](#prediction-engine---java--sql)
- [Containerisation](#containerisation)
  - [Local Services (Docker Compose)](#local-services-docker-compose)
- [Project Management and Communication](#project-management-and-communication)
- [Contributing and Updates](#contributing-and-updates)


## Overview

This document describes the complete toolchain used to develop, test and deliver the OSM to Kuwaiba
Application. This is intended to be used as a reference and to ensure consistency for group members.

The system takes in Open Street Map data, this is then stored and processed in a PostGIS database.
Fiber infrastructure prediction is run using SQL queries and java. Results are exported via a
REST API (JSON) or CSV files for use in Kuwaiba.

## Development Environment

|Tool|Version|Purpose|
|----|-------|-------|
|Eclipse|Latest|Primary Java IDE|
|Intellij Idea|Latest|Secondary Java IDE|
|Visual Studio Code|Latest|General Editing, config files, SQL, documentation|
|Java JDK|21|Java runtime and compiler|
|Docker Desktop|Latest|Local Container Management|
|pgAdmin|Latest|PostGIS Database GUI|


## Version Control

|Tool|Detail|
|----|------|
|Platform|GitHub|
|Repository|`com617-industrial-2025-1/com617-kuwaiba-inventory-1`|
|Commit Style| [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)|
|Strategy|Github Flow (including feature branches off of `main`)|

## Data Ingestion - Open Street Map

|Tool|Purpose|
|----|-------|
|[HOT Export Tool](https://export.hotosm.org/)|Exporting custom area OSM extracts|
|[Geofabrik](https://download.geofabrik.de/)|Downloading larger regional OSM extracts|
|[osm2pgsql](https://osm2pgsql.org/)|Importing OSM data into PostGIS|

### Ingestion Workflow 

1. Data is extracted in the form of a `.xml` file and placed in `container-fs/data/`.
2. The `importer` Docker service runs `osm2pgsql`.
3. `osm2pgsql` creates queryable tables in database.
4. On Spring Boot startup `createtables.sql` is run to clean and process raw osm data into
   required tables e.g. `cleaned_buildings`, `building_drop_points`.

## Database - PostGIS

|Tool|Version|Purpose|
|----|-------|-------|
|PostgreSQL|15|Relational Database Engine|
|PostGIS|3.4|Spatial Extension for PostgreSQL|
|pgRouting|3.x|Road network routing extension|
|Spring Data JPA/Hibernate Spatial| |Java to PostgreSQL connectivity with spatial type support|

### Spatial Reference System 
All geometries in the database use **EPSG:3857**.

Transformation to this coordinate system can be done with the PostGIS command `ST_Transform`.

### Database Objects / Tables 

|Object|Description|
|------|-----------|
| `planet_osm_polygon` | Raw OSM building polygons (created by osm2pgsql) |
| `planet_osm_line` | Raw OSM road linestrings (created by osm2pgsql) |
| `cleaned_buildings` | Validated and enriched building polygons with street names |
| `cleaned_roads` | Simplified and connected road network with islands removed |
| `building_drop_points` | One snapped drop point per building, on the building's exterior ring |
| `noded_streets` | Topologically noded road network for pgRouting |
| `raw_uprns` | Imported UPRN CSV data (lat/lon and UPRN number) |
| `linked_buildings` | Buildings spatially matched to a UPRN |
| `network_points` | Predicted network infrastructure (POLE, CABINET, AGGREGATOR, EXCHANGE) |
| `network_connections` | Predicted cable connections (DROP, FEEDER, DISTRIBUTION, TRUNK) |

### Database Connection (via Docker) 
```
Host:     localhost
Port:     5432
Database: osm
Username: osmuser
Password: osmpass
```

## Prediction Engine - Java & SQL

Prediction Logic is split between:
- **SQL Queries**: All spatial analysis runs natively in PostGIS and pgRouting vis native query
  methods in Spring Data JPA repository interfaces. No spatial computation happens in Java.
- **Service Layer (Java)**: Orchestrates which repository methods are called and in what order.
- **REST Controllers**: Expose prediction triggers and results as HTTP endpoints.

Key Algorithms:
- `ST_ClusterKMeans`: groups buildings/poles/cabinets into clusters for placements.
- `ST_ClosestPoint` and `CROSS JOIN LATERAL`: snapping cluster centroids to nearest roads.
- `pgr_dijkstra` with `ST_LineMerge`: calculating road following cables between points.


## Containerisation

|Tool|Version|Purpose|
|----|-------|-------|
|Docker|Latest|Containerise Services|
|Docker Compose|v2|Manage Local multi-service environment|

### Local Services (Docker Compose) 

Running `docker compose up` will spin up:
|Service|Image|Purpose|
|-------|-----|-------|
| `db` | `pgrouting/pgrouting:15-4` | PostgreSQL 15 + PostGIS + pgRouting database |
| `importer` | `iboates/osm2pgsql` | One-shot OSM data import (exits on completion) |
| `app` | Built from `Dockerfile` | Spring Boot REST API on port 8080 |
| `pgadmin` | `dpage/pgadmin4` | Database GUI on port 8888 |
| `kuwaiba` | `neotropic/kuwaiba:v2.1-nightly` | Kuwaiba network inventory on port 8081 |

Startup ordering is enforced via `depends_on` with health check conditions. The `app` service waits
for `db` to pass its health check and for `importer` to exit successfully before starting.

## Project Management and Communication

|Tool|Purpose|
|----|-------|
|GitHub Projects|Sprint and Task Management|
|Whats App|Project Communication|

## Contributing and Updates

This document should be kept up to date as the toolchain evolves including if a tool is introduced,
removed, or changed.

This document is version controlled alongside the codebase. Historical changes can be viewed in
the git history for this file.