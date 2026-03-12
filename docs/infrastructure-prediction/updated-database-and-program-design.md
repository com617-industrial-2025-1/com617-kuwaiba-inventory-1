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
|01234|CAB-001-012|9876|CABINET|POINT(1.0123, -1.0987)|
|01235|AGG-002-001|101|AGGREGATOR|POINT(1.0224, 2.2222)|

### `network_connections`
