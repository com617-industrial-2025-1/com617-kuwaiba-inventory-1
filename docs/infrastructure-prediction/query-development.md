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

WINDOW FUNCTIONS?????????


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
groups of 12. This creates a temporary derived table that matches building_id with cluster_id and
the buildings geom POINT. 

`ST_Collect` aggregates points into a `GEOMETRYCOLLECTION` which `ST_Centroid` then uses to 
calculate the geographic center of the points.

This central point becomes the pole location as a geom POINT and is inserted into network_points
with type `POLE`.

`@Modifying` is used to let spring boot know that the uery modifies the database. This means
the query can also be used in a `@Transactional` that the service uses.

### 



## Top Down Connection Prediction