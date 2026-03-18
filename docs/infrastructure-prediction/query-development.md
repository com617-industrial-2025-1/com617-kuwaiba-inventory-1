# Query Development

Queries should be developed to fit into the Workflow laid out in [database-and-program-design](./database-and-program-design.md).
As well as queries that may need to be developed for use of a REST API.

## Important PostGIS Concepts

**Spatial Indexes**

- This makes using a spatial database for large datasets possible. 
- **GiST**: Generalized Search Tree is a generic form of indexing for multi-dimensional data.
  - GiST is the most common versatile spatial index method and has good query performance.
  - Syntax for building a GiST index:
```sql
CREATE INDEX [indexname] ON [tablename] USING GIST ( [geometryfield] );
```
- **BRIN**: Block Range Index is a general purpose index method.
- Syntax for building BRIN index:
```sql
CREATE INDEX [indexname] ON [tablename] USING BRIN ( [geome_col] );
```
- **SpGiST**

GiST Indexes are performant as long as size doesn't exceed the amount of RAM available for the
database. For very large tables BRIN index can be considered as an alternative.

**Aggregate Functions**

**`ST_ClusterKMeans`**


**`ST_AsText`** is able to convert geometry points and linestrings into wkt (well known text). A
WKT reader can then be implemented to convert the wkt into a JTS Point that can be stored in geom
fields.

**WINDOW FUNCTIONS**
A Window function performs a calculation across a set of table rows that are related to the
current row. It is similar to an aggregate function like `COUNT()` however window functions do not
cause the rows to become grouped into a single output row.

## Creating Network Tables
```sql
CREATE TABLE network_points (
    id        BIGSERIAL PRIMARY KEY,
    external_id VARCHAR,
    parent_id BIGINT,
    type      VARCHAR,
    geom      geometry(Point, 3857)
);

CREATE TABLE network_connections (
    id          BIGSERIAL PRIMARY KEY,
    external_id VARCHAR,
    start_id    BIGINT,
    end_id      BIGINT,
    link_type   VARCHAR,
    geom        geometry(LineString, 3857)
);
```

## Bottom Up Point Prediction

### Clustering Buildings and Inserting Pole Points

```sql
INSERT INTO network_points (type, geom)
        SELECT 
            'POLE',
            ST_Centroid(ST_Collect(geom))
        FROM (
            SELECT
                ST_ClusterKMeans(geom,
                    CAST(CIEL(COUNT(*) OVER() / 12.0) AS INTEGER)
                ) OVER () AS cluster_id,
                geom
            FROM building_drop_points
            ) clustered
        GROUP BY cluster_id
```

Clusters with `ST_ClusterKMeans`: taking total number of building drop points and dividing into
groups of 12. This creates a subquery result that matches building_id with cluster_id and
the buildings geom POINT. 

`ST_Collect` aggregates points into a `GEOMETRYCOLLECTION` which `ST_Centroid` then uses to 
calculate the geographic center of the points.

This central point becomes the pole location as a geom POINT and is inserted into network_points
with type `POLE`.

`@Modifying` is used to let spring boot know that the uery modifies the database. This means
the query can also be used in a `@Transactional` that the service uses.

### Other Clustering of Points

All other clustering relies on the same type of query with the difference that it gathers points
from the `network_points` table and all are grouped into 8 as per the requirements. Below is an
example of the Cabinet clustering. **Note** that Aggregator and Exchange clustering works the same
way and can be viewed in `workflow/`.

```sql
INSERT INTO network_points(type, geom)
        SELECT 
            'CABINET',
            ST_Centroid(ST_Collect(geom))
        FROM (
            SELECT 
                ST_ClusterKMeans(geom,
                    CAST(CEIL(COUNT(*) OVER() / 8.0) AS INTEGER)
                ) OVER () AS cluster_id,
                geom
            FROM network_points
            WHERE type = 'POLE'
        ) clustered 
        GROUP BY cluster_id
```

### Updating Parents

These are a set of SQL queries designed to match up the parents to the children in the hierarchy
of the network. All follow a similar format. Below is the updating pole parents query.

```sql
UPDATE network_points poles
        SET parent_id = nearest_id
            FROM (
                SELECT
                    p.id AS pole_id,
                    c.id
                    FROM network_points p
                    CROSS JOIN LATERAL (
                        SELECT id
                        FROM network_points
                        WHERE type = 'CABINET'
                        ORDER_BY ST_Distance(geom, p.geom)
                        LIMIT 1
                    ) c
                    WHERE p.type = 'POLE'
            ) nearest
            WHERE poles.id = nearest.pole_id
```

This updates the `network_points` table. From the innermost query outward:
- For each pole the nearest cabinet is selected and `id` returned.
- The cabinet id is matched with the pole id in a temporary subquery result.
- Then the `network_points` table is updated by looking for the pole id and adding the cabinet
  id as the `parent_id`



## Top Down Connection Prediction