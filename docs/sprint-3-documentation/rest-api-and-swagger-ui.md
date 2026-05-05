# REST API and Swagger UI

Relevant Files:
- code in the [rest folder](https://github.com/com617-industrial-2025-1/com617-kuwaiba-inventory-1/tree/main/osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/java/ac/uk/solent/com617/kuwaiba/osm_to_kuwaiba/rest)

- in the [pom.xml](https://github.com/com617-industrial-2025-1/com617-kuwaiba-inventory-1/blob/main/osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/pom.xml) is the dependency for the swagger ui

## Overview

The application exposes a REST API. All endpoints return JSON and are browsable via Swagger UI at:
```
http://localhost:8080/swagger-ui/index.html
```

The API is organised into four groups of endpoints:

| Group | Base Path | Purpose |
|-------|-----------|---------|
| Network Points | `/network` | Raw predicted network points |
| Network Connections | `/network` | Raw predicted network connections |
| Kuwaiba Export | `/kuwaiba-network` | Network data formatted for Kuwaiba |
| Prediction | `/predict` | Trigger the prediction pipeline |

## Swagger UI

Swagger UI is provided by the `springdoc-openapi` library and added as a dependency in `pom.xml`:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.x.x</version>
</dependency>
```

Swagger UI automatically discovers all `@RestController` classes and generates an interactive API 
browser from them. No additional configuration is required as any endpoint with `@GetMapping`,
`@PostMapping` etc. is automatically documented.

## Pagination

Pagination is supported on the Kuwaiba Export Endpoints only. These accept two query parameters:

| Parameter | Default | Description |
|-----------|---------|-------------|
| `pageNo` | `0` | Zero-indexed page number |
| `pageSize` | `10` | Number of results per page |

Pagination is implemented using Spring Data's `Pageable` and `PageRequest`.

The `/network` endpoints do not use pagination. They return all results as a plain `List` in a
single response. This means the response for large datasets can be very large.

## Network Points Endpoints

These are implemented in `NetworkController.java` and returns raw NetworkPoint entities serialised
to JSON. The output is a JSON array of entity objects with embedded geometry and not a GeoJSON
FeatureCollection.

### `GET /network/points`

Returns all predicted network points

Example Response:

```json
[
  {
    "id": 1,
    "type": "POLE",
    "externalId": "POLE-3f4a1b2c-...",
    "geom": { "type": "Point", "coordinates": [-1.4023, 50.9245] },
    "parentId": 42
  }
]
```

### `GET /network/points/type`

Returns network points filtered by type.

**Query Parameters**: `type`(required: `POLE`, `CABINET`, `AGGREGATOR`, `EXCHANGE`)

Example:
```
GET /network/points/type?type=POLE
```

## Network Connections Endpoints

These are implemented in `NetworkController.java` and reeturn all `NetworkConnection` entities
serialised to JSON with embedded GeoJSON geometry. 

### `GET /network/connections`

Returns all predicted network connections.

Example reseponse:
```json
[
  {
    "id": 1,
    "linkType": "DROP",
    "startId": 12,
    "endId": 8045,
    "externalId": "DROP-7f57d359-...",
    "geom": { "type": "LineString", "coordinates": [[...], [...]] },
    "osmId": 755449769,
    "streetName": "Midanbury Lane"
  }
]
```

### `GET /network/connections/type`

Returns connections filtered by link type.

Query Parameters: `linkType` (required: `TRUNK`, `DISTRIBUTION`, `FEEDER`, `DROP`)

## Kuwaiba Export Endpoints

These are implemented in `KuwabaNetworkController.java` and return data formatted as `KuwaibaClass`
objects using the `org.entimoss.kuwaiba.provisioning` model library.

### `GET /kuwaiba-network/points`

Returns network points wrapped in Kuwaiba class objects with class names and parent location
hierarchy.

### `GET /kuwaiba-network/connections`

Returns network connections wrapped in Kuwaiba connection objects.

### `GET /kuwaiba-network/kuwaibaRequisition`

This is the primary export endpoint and returns a complete `KuwaibaProvisioningRequisition`
containing:
- Static templates (pole types, cabinet types etc.) from `ProjectConstants`
- Static location objects
- All buildings from `linked_buildings` with UPRN and street attributes
- All network connections with `endpointA` and `endpointB` populated.

**DROP connection handling**: DROP connections reference `building_drop_points` rather than
`network_points` The controller resolves `endpointB` for DROP connections by:

1. Looking up the buildings `osm_id` in `linked_buildings`: if found the building is enriched with
   its UPRN and street name.
2. Falling back to `cleaned_buildings` for buildings without a UPRN match which are named 
   `Building <osm_id>`

Example Response (DROP connection with UPRN):
```json
{
  "connectionClass": {
    "className": "WireContainer",
    "name": "DROP-7f57d359-2c90-4a11-91a8-c0a64526c85c",
    "attributes": { "link_type": "DROP", "street_name": "Midanbury Lane" }
  },
  "endpointA": {
    "className": "Pole",
    "name": "POLE-ff5d4964-998a-4c99-86db-906b706d697c"
  },
  "endpointB": {
    "className": "Building",
    "name": "100060694767",
    "attributes": { "uprn": "100060694767", "street": "Dell Road" }
  }
}
```

Example Response (`endpointB` DROP connection without UPRN):
```json
{
  "endpointB": {
    "className": "Building",
    "name": "Building 755449769",
    "attributes": {}
  }
}
```

## Prediction Endpoint

These endpoints are implemented in `PredictionController.java`

### `POST /predict/all`

This triggers the full infrastructure prediction pipeline. This endpoint should only be called once
after the OSM data has been imported and thee application has started. Calling it a second time will 
attempt to insert duplicate data.

The pipeline runs the following steps in order:
1. Insert POLE clusters (K-Means on `building_drop_points`, 12 buildings per pole)
2. Update `building_drop_points` parent IDs to nearest pole
3. Insert CABINET clusters (K-Means on poles, 8 poles per cabinet)
4. Update pole parent IDs to nearest cabinet
5. Insert AGGREGATOR clusters (K-Means on cabinets, 8 cabinets per aggregator)
6. Update cabinet parent IDs to nearest aggregator
7. Insert EXCHANGE (centroid of all aggregators)
8. Update aggregator parent IDs to nearest exchange
9. Insert TRUNK connections (road-following, exchange to aggregators)
10. Insert DISTRIBUTION connections (road-following, aggregators to cabinets)
11. Insert FEEDER connections (road-following, cabinets to poles)
12. Insert DROP connections (straight line, poles to buildings)

## Geometry Serialisation

Raw PostGIS geometry objects (JTS `Point`, `LineString`, `MultiLineString`) are not serialisable to 
JSON by default. Custom Jackson serialisers were implementeed in the `config` folder.

| Serialiser | Handles | Output |
|-----------|---------|--------|
| `PointSerializer` | `org.locationtech.jts.geom.Point` | GeoJSON Point object |
| `LineStringSerializer` | `org.locationtech.jts.geom.LineString` | GeoJSON LineString object |
| `MultiLineStringSerializer` | `org.locationtech.jts.geom.MultiLineString` | GeoJSON MultiLineString object |

For more details on geometry serialisation see [GeoJSON Serialisation](./geojson-serialisation.md)
