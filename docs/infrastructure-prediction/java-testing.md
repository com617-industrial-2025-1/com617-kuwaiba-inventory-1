# Java Testing

There are two ways of testing the java application:
- Testing each step independently with mock data.
- Test the full pipeline end to end.

Both types of tests will be implemented in the project.

Testing will be completed for each repository and each service

- [ ] Repository
  - [ ] `BuildingDropPointRepository`
    - [ ] Pole creation for each cluster.
    - [ ] Only creates poles.
    - [ ] Doesn't create poles when no buildings exist.
    - [ ] Set Parent Id on every building.
    - [ ] Building Parent Id links to an existing pole.
  - [ ] `CleanedBuildingRepository`
    - [ ] 
  - [ ] `CleanedRoadRepository`
  - [ ] `NetworkConnectionRepository`
  - [ ] `NetworkPointRepository`
  - [ ] `NodedStreetsRepository`
- [ ] Service
  - [ ] `ClusteringService`
  - [ ] `RoutingService`

`JdbcTemplate` has to be used to insert test data into the database as PostGIS geometry data has to
be added in SQL format.

`@Transactional` applies to all tests in the class and doesn't need to be referenced before each
test. It is implied where it is defined.

**Meaningful Test Data**
A set of two streets of test data can be used to replicate a real scenario. Below is an example of
the test data that is in `BuildingDropPointRepositoryTests`:
```sql
INSERT INTO building_drop_points (building_id, geom) VALUES
    (1,  ST_GeomFromText('POINT(442000 5425000)', 3857)),
    (2,  ST_GeomFromText('POINT(442012 5425000)', 3857)),
    (3,  ST_GeomFromText('POINT(442024 5425000)', 3857)),
    (4,  ST_GeomFromText('POINT(442036 5425000)', 3857)),
    (5,  ST_GeomFromText('POINT(442048 5425000)', 3857)),
    (6,  ST_GeomFromText('POINT(442060 5425000)', 3857)),
    (7,  ST_GeomFromText('POINT(442072 5425000)', 3857)),
    (8,  ST_GeomFromText('POINT(442084 5425000)', 3857)),
    (9,  ST_GeomFromText('POINT(442096 5425000)', 3857)),
    (10, ST_GeomFromText('POINT(442108 5425000)', 3857)),
    (11, ST_GeomFromText('POINT(442120 5425000)', 3857)),
    (12, ST_GeomFromText('POINT(442132 5425000)', 3857)),
    (13, ST_GeomFromText('POINT(443000 5425000)', 3857)),
    (14, ST_GeomFromText('POINT(443012 5425000)', 3857)),
    (15, ST_GeomFromText('POINT(443024 5425000)', 3857)),
    (16, ST_GeomFromText('POINT(443036 5425000)', 3857)),
    (17, ST_GeomFromText('POINT(443048 5425000)', 3857)),
    (18, ST_GeomFromText('POINT(443060 5425000)', 3857)),
    (19, ST_GeomFromText('POINT(443072 5425000)', 3857)),
    (20, ST_GeomFromText('POINT(443084 5425000)', 3857)),
    (21, ST_GeomFromText('POINT(443096 5425000)', 3857)),
    (22, ST_GeomFromText('POINT(443108 5425000)', 3857)),
    (23, ST_GeomFromText('POINT(443120 5425000)', 3857)),
    (24, ST_GeomFromText('POINT(443132 5425000)', 3857))
```
Each street has 12 houses placed 12 meters apart.