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
  - [Testing](#testing)
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
|Strategy|Github Flow (including feature branches off of `main`)|

*A point of improvement that could be mentioned in report is the use of 
[Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) commit style.*

## Data Ingestion - Open Street Map

|Tool|Purpose|
|----|-------|
|osm2streets|Source for OSM data extracts|
|osm2pgsql|Importing OSM data into PostGIS|

### Ingestion Workflow 

## Database - PostGIS

|Tool|Version|Purpose|
|----|-------|-------|
|PostgreSQL|15|Relational Database Engine|
|PostGIS|3.3|Spatial Extension for PostgreSQL|
|TODO|xxx|Java to PostgreSQL connectivity|

### Spatial Reference System 
All geometries in the database use **EPSG:3857**.

Transformation to this coordinate system can be done with the PostGIS command `ST_Transform`.

### Database Objects / Tables 

|Object|Description|
|------|-----------|
TODO complete list of tables and descriptions

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
- **SQL Queries**: Spatial analysis within PostGIS.
- **Java Application Layer**: Orchestration, Business Logic, API Serving.



### Testing 

## Containerisation

|Tool|Version|Purpose|
|----|-------|-------|
|Docker|Latest|Containerise Services|
|Docker Compose|v2???|Manage Local multi-service environment|

### Local Services (Docker Compose) 

Running `docker compose up` will spin up:
|Service|Image|Purpose|
|-------|-----|-------|
|`PostGIS`|`postgis/postgis:15-3.3`|PostGIS database|


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