# README osm-to-kuwaiba

This application is written using spring boot. Archetype generated using https://start.spring.io/

with settings

java version: java 21
language: java
build: maven
packaging: jar
configuration: propeties

additional dependencies:

* Spring Web : Build web, including RESTful, applications using Spring MVC. Uses Apache Tomcat as the default embedded container.
*Spring Data JPA SQL: Persist data in SQL stores with Java Persistence API using Spring Data and Hibernate.
*PostgreSQL Driver SQL: A JDBC and R2DBC driver that allows Java programs to connect to a PostgreSQL database using standard, database independent Java code.


## spring boot and postgis tutorials 

Spring boot and PostGIS

* [How to Implement Geospatial Queries in Spring Boot with PostgreSQL - Alexander Obregon](https://medium.com/@AlexanderObregon/how-to-implement-geospatial-queries-in-spring-boot-with-postgresql-bce52e7ffafa)

* [Building a Location-Based REST API with OpenStreetMap, PostGIS, and Spring Boot](https://www.antanaskovic.com/en/blog/building-location-based-rest-api-with-osm-postgis-and-spring-boot)

* [Spring Boot and PostGIS](https://bocharoviliyav.blog/java/2021/02/03/Spring-Boot-Postgis-Example.html) (a bit more complicated)

For basic Spring DATA JPA see

[Comprehensive Guide to Spring Data JPA with Example Codes](https://vijayskr.medium.com/comprehensive-guide-to-spring-data-jpa-with-example-codes-8db0c9683b0f)

[Introduction to spring data jpa (including basic JDBC queries example)](https://codesignal.com/learn/courses/persisting-data-with-spring-data-jpa/lessons/introduction-to-spring-data-jpa)


```
-- ST_MakePoint(:lng, :lat) lat=1.4049&lng=-50.9105&radius=500000
-- "SELECT * FROM planet_osm_point p WHERE ST_DWithin(p.way, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326), :radius) LIMIT 10"
--  SELECT * FROM planet_osm_point p WHERE ST_DWithin(p.way, ST_SetSRID(ST_MakePoint(50.9105,-1.4049), 4326), 500000) LIMIT 10
SELECT * FROM planet_osm_point p LIMIT 10
```

## building and running

to build the application without tests use

```
 mvn clean install -DskipTests
```
note that if you don't have maven installed on your PC, there is a maven runner included with  the project.

You can use the runner on windows instead of `mvn`

```
 ./mvnw.cmd
```

To build with tests use

```
 mvn clean install
```

to run the tests you will need to have the database already turned using the database docker project

The database credentials are set in the application.properties file

To run as a an application use

```
 mvn spring-boot:run
```

because spring boot web is included in classpath, the tomcat web server sill start at

[http://localhost:8080/](http://localhost:8080/)

This just serving content from src/main/resorces/static/index.html

You will need to look at spring web tutorials to see how to set up dynamic web pages and rest api's etc


points: http://localhost:8080/api/points/near?latitude=1.4049&longitude=-50.9105&distance=500

all points: http://localhost:8080/api/points/all

locations: http://localhost:8080/api/locations/near?longitude=50.9105&latitude=1.4049&distance=400