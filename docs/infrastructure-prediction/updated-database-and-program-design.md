# Updated Database and Program Design

The framework for the java program has been changed. We are now using [Spring Boot](https://spring.io/projects/spring-boot)
as this simplifies the connection between the database and the java logic.

## Database Design
Along with the new tables that are created by the data extraction and data processing teams the
infrastructure prediction team will utilize two new tables:
- `network_points`
- `network_connections`

Sample data from these two tables can be seen below. Multiple tables from the previous design have
been coalesced into one `network_points` table.

### `network_points`
|id(Long)PK|external_id(String)|parent_id(Long)FK|type(PointType)|geom(Point)|
|----------|-------------------|-----------------|---------------|-----------|
|01234|CAB-012|9876|CABINET|POINT(1.0123, -1.0987)|
|01235|AGG-002|101|AGGREGATOR|POINT(1.0224, 2.2222)|

**Note**: PointType is as a program defined enumerator.

### `network_connections`
|id(Long)PK|external_id(String)|start_id(Long)FK|end_id(Long)FK|link_type(LinkType)|geom(LineString)|
|----------|-------------------|----------------|--------------|-------------------|----------------|
|01234|LNK-CAB-012-AGG-002|01234|01235|DISTRIBUTION|LINESTRING(...)|

## Java Program Design
With the new Spring Boot framework functionality is split into four distinct layers each with a
clear defined responsibility:
- **Models**: Defines the applications data as Java classes. These are directly mapped onto the
  database tables using JPA annotations. Hibernate reads these definitions on startup and creates or
  updates the tables. (No manual SQL needed)
- **Repositories**: This is the only part of the application permitted to interact directly with the
  database. Each repository is a Java interface that extends `JpaRepository`. Simple queries are
  created automatically by Spring from the method name. More complex PostGIS queries are defined
  explicitly.
- **Services**: This contains the business logic and decisions about what to do with data, in what
  order, how to handle problems. This is the only layer that is allowed to call multiple
  repositories and combine their results. Each service is managed by Spring through the `@Service`
  annotation. Critical multistep operations are wrapped with `@Transaction` to make sure if there is
  a failure that all steps are rolled back.
- **REST**: This layer exposes the applications functionality as HTTP endpoints that can be
  triggered from a browser. The REST controller is marked with `@RestController` and calls the
  appropriate service.
