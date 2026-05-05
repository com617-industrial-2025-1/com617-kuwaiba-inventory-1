# Hampshire Pipeline Test

## Overview

**Dataset**: Hampshire, England (`hampshire.osm.pbf`)
**Source**: Geofabrik

## 1. OSM Data Import

### 1.1 Importer Service Completion

The `importer` docker service ran `osm2pgsql` against the `hampshire.osm.pbf` and exited
successfully.

**Evidence of importer logs showing successful completion**

### 1.2 Raw OSM Table Record Counts

After the import, the raw OSM tables were populated. The following counts were recorded in
pgAdmin:

```sql
SELECT COUNT(*) FROM planet_osm_polygon WHERE building IS NOT NULL;
SELECT COUNT(*) FROM planet_osm_line WHERE highway IS NOT NULL;
```

**Evidence of pgadmin query results**

## 2. Data Preparation (createtables.sql)

The Spring Boot Application ran `createtables.sql` on startup to clean and process the raw data.

### 2.1 Application Startup Log

**Evidence of spring boot logs showing schema initialization completing successfully**

### 2.2 Cleaned Data Record Counts

The following cleaned data record counts were gathered from pgAdmin

```sql
SELECT COUNT(*) FROM cleaned_buildings;
SELECT COUNT(*) FROM cleaned_roads;
SELECT COUNT(*) FROM building_drop_points;
SELECT COUNT(*) FROM linked_buildings;
SELECT COUNT(*) FROM noded_streets;
```

**Evidence of pgadmin query results**

## 3. Infrastructure Prediction

The prediction was triggered via `POST /prediction/runAll`

### 3.1 Prediction Completion Log

**Evicence of Spring Boot logs showing the full prediction pipeline completing**

### 3.2 Network Point and Connection Record Counts
The following queries were used in pgAdmin to gain evidence of the prediction pipeline completing.

```sql
SELECT type, COUNT(*) FROM network_points GROUP BY type ORDER BY type;
```

**Evidence of pgadmin query results**

```sql
SELECT link_type, COUNT(*) FROM network_connections GROUP BY link_type ORDER BY link_type;
```

**Evidence of pgadmin query results**

## 4. API Verification

The Swagger UI was accessible at `http://localhost:8080/swagger-ui/index.html` to confirm the
REST API was running

**Screenshot of Swagger UI running showing all endpoints**

### 4.2 Network Point EndPoint

`GET /network/points?pageNo=0&pageSize=10`

**Screenshot of browser showing JSON response**

### 4.3 Kuwaiba Requisition Sample

`GET /kuwaiba-network/kuwaibaRequisition?pageNo=0&pageSize=10`

**Screenshot of browser showing JSON response**

## 5. Visualisation

### 5.1 QGIS Network Map

The GeoJSON output was loaded into QGIS to visually verify the predicted network covers the
Hampshire area correctly. 

**Screenshot of QGIS**

## 6. Summary

The full pipeline ran successfully against the Hampshire dataseet, demonstrating that the pipeline
works with different osm input.

**Note** due to the scale of this dataset, the pipeline took approximately **TODO** to complete.
For live demonstrations a smaller area will be used which produces equivalent results in a fraction
of the time.


