# GeoFabrik Hampshire Pipeline Test

## Overview

**Dataset**: Hampshire, England (`hampshire.osm.pbf`)
**Source**: Geofabrik

## 1. Data Export From GeoFabrik

The data from GeoFabrik comes in predefined areas with the smallest area being county level. Due to 
this the data gathered was from the whole of the Hampshire region.

![geofabrik data gathering](./images/geofabrik/geofabrik-data-gathering.png)

This data was then placed in the `container-fs/data` folder where is was the only data source.

![geofabrik export file explorer](./images/geofabrik/geofabrik-export-file-explorer.png)

## 2. OSM Data Import

### 2.1 Importer Service Completion

The `importer` docker service ran `osm2pgsql` against the `hampshire.osm.pbf` and exited
successfully.

![GeoFabrik Importer Completion](./images/geofabrik/geofabrik-importer-logs.png)

### 2.2 Raw OSM Table Record Counts

After the import, the raw OSM tables were populated. The following counts were recorded in
pgAdmin:

```sql
SELECT COUNT(*) FROM planet_osm_polygon WHERE building IS NOT NULL;
SELECT COUNT(*) FROM planet_osm_line WHERE highway IS NOT NULL;
```

**Evidence of pgadmin query results**

## 3. Data Preparation

The Spring Boot Application ran `createtables.sql` on startup to clean and process the raw data.

### 3.1 Application Startup Log

**Evidence of spring boot logs showing schema initialization completing successfully**

### 3.2 Cleaned Data Record Counts

The following cleaned data record counts were gathered from pgAdmin

```sql
SELECT COUNT(*) FROM cleaned_buildings;
SELECT COUNT(*) FROM cleaned_roads;
SELECT COUNT(*) FROM building_drop_points;
SELECT COUNT(*) FROM linked_buildings;
SELECT COUNT(*) FROM noded_streets;
```

**Evidence of pgadmin query results**

## 4. Infrastructure Prediction



### 4.1 Prediction Trigger

The prediction was triggered via `POST /predict/all`

**Image of swagger ui trigger success**

### 4.2 Network Point Record Count

The following query was used in pgAdmin to gain evidence of the prediction pipeline completing.

```sql
SELECT type, COUNT(*) FROM network_points GROUP BY type ORDER BY type;
```

**Evidence of pgadmin query results**

### 4.3 Network Connection Record Count

The following query was used in pgAdmin to gain evidence of the prediction pipeline completing.

```sql
SELECT link_type, COUNT(*) FROM network_connections GROUP BY link_type ORDER BY link_type;
```

**Evidence of pgadmin query results**

## 5. API Verification

The Swagger UI was accessible at `http://localhost:8080/swagger-ui/index.html` to confirm the
REST API was running

**Screenshot of Swagger UI running showing all endpoints**

### 5.1 Network Points Endpoint

`GET /network/points?pageNo=0&pageSize=10`

**Screenshot of browser showing JSON response**

### 5.2 Network Connections Endpoint

### 5.3 Kuwaiba Requisition Sample

`GET /kuwaiba-network/kuwaibaRequisition?pageNo=0&pageSize=10`

**Screenshot of browser showing JSON response**

## 6. Visualisation

### 6.1 QGIS Network Map

The GeoJSON output was loaded into QGIS to visually verify the predicted network covers the
Hampshire area correctly. 

**Screenshot of QGIS**

## 7. Summary

The full pipeline ran successfully against the Hampshire dataseet, demonstrating that the pipeline
works with different osm input.


## 8. Pipeline Time Duration

|Stage|Start Time|End Time|Duration|
|-----|----------|--------|--------|
|Importer|22:38:59|22:39:41|42s|
|Data Preparation (createtables.sql)||||
|Prediction (predict/all)||||

**Note** due to the scale of this dataset, the pipeline took approximately **TODO** to complete.
For live demonstrations a smaller area will be used which produces equivalent results in a fraction
of the time.

### 8.1 Screenshot Evidence

**Importer Time**

![geofabrik importer time](./images/geofabrik/geofabrik-importer-time.png)

**Data Preparation Time**



**Prediction Time**
