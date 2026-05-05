# Spring Boot Docker Documentation


Initial Notes:

This involves documenting how the Spring Boot Application is containerised into its own docker 
container that can be run.

Files involved (from the osm-to-kuwaiba project):
- [docker-compose.yml](../../osm-to-kuwaiba/docker-compose.yml)
- [Dockerfile](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/Dockerfile)

## Overview

The Spring Boot application runs as a Docker container as part of the Docker Compose stack alongside
the database, OSM importer, pgAdmin and Kuwaiba. This means a user only needs Docker Desktop
installed as Java and/or Maven is not required.

## The `Dockerfile`

The multi-stage `Dockerfile` was used to keep the final image as small as possible. The container
build happens in two stages:

### Stage 1 - Build

The base image for this stage is an image that has Maven and JDK 21 installed. This is used as
everything needed to compile and package the application is available here.

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
```

The `pom.xml` and Maven wrapper are copied before the source code. This is the intentional order as
Docker builds layers in order and caches them. Since `pom.xml` changes far less than the source
code files, Maven will only redownload when `pom.xml` changes making future builds faster.

The source code is then copied and Maven compiles everything into a JAR in `/app/target/`. The tests
are skipped because they are not needed for the final product and they also require a PostGIS
database to run which does not exist when the JAR is built.

### Stage 2 - Runtime

A fresh minimal base image is used that just contains the Java Runtime Environmeent. This makes the
final image smaller than if the build image was to be used directly. 

```dockerfile
FROM eclipse-temurin:21-jre
```

The compiled JAR is copied across from the build stage and none of the source code or Maven files
are copied over with it. The jar is exposed on port 8080.

## `app` container in `docker-compose.yml`

the build context is pointed to the `osm-to-kuwaiba-sprint-boot` folder where the `Dockerfile` and
`pom.xml` are. This means docker uses this folder as the root for the instructions in the
`Dockerfile`. 

The container `depends_on` the database passing its healthcheck (meaning PostgreSQL is ready to 
accept connections) and also waits for the `importer` to finish loading the OSM data.

`restart: on-failure` means the container will restart is the Spring Boot process crashes but will
not restart if the container is deliberately stopped.

## Overriding `application.properties` with Environment Variables

Spring Boot automatically maps environment variables to their application properties as seen:
```yaml
environment:
    SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/osm
    SPRING_DATASOURCE_USERNAME: osmuser
    SPRING_DATASOURCE_PASSWORD: osmpass
    UPRN_CSV_PATH: /data/uprns.csv
```

## Volume Mount

```yaml
volumes:
  - ./container-fs/data:/data
```

The `container-fs/data` folder on the host (contains the OSM data file (.osm.pbf, .xml, .pbf) and
`uprns.csv`) is mounted into the container at `/data`. This is the same mount used by the importer 
service so both the importer and the app have access to the same files. 

