# Spring Boot Startup and Initialisation

## 1. Overview
When starting the Spring Boot service, the system automatically verifies the database state, initialises the required spatial schemas, and seeds the UPRNs if they are missing. This ensures the environment is ready for network prediction without manual database intervention.

## 2. Automated Schema Initialisation
The startup sequence begins with the `SchemaInitialization` service. This component manages the structure of the PostGIS database.

*   **Script Execution:** The service automatically runs `schema.sql` to define the core table structures and `createtables.sql` to perform initial data cleaning on the imported OSM data.
*   **Spatial Repair:** During this phase, raw OpenStreetMap polygons are validated and repaired using `ST_MakeValid`.
*   **Buildings as Points:** The system executes the "Drop Point" logic, identifying the specific coordinate on a building's exterior closest to the road to facilitate network connections.

## 3. UPRN Data Seeding
Once the schema is ready, the `UprnDataInitializer` service handles the ingestion of property records.

*   **Existence Check:** To prevent redundant processing, the system checks if the `raw_uprns` table contains data.
*   **Bulk Import:** If empty, it triggers the `UprnImportService`, which uses a high-speed binary stream to upload the `uprns.csv` file into PostgreSQL.
*   **Spatial Linking:** The service then executes a spatial join to populate the `linked_buildings` table, effectively mapping every building's `osm_id` to its official `uprn` identifier.

## 4. Metadata and ID Synchronisation
The final phase of startup prepares the data for export by ensuring all geographic points are correctly tagged for the Kuwaiba inventory.

*   **OSM ID Mapping:** The startup logic ensures that all `AGGREGATOR` points in the network map carry the original `osm_id` from the building footprints. This is critical for the export controller to retrieve UPRN names later.
*   **Street Enrichment:** Fibre connections are spatially queried against the road network to populate the `street_name` metadata, ensuring no `null` values are present in the final provisioning requisition.



## 5. Relevant Project Files
To review or modify the startup logic, refer to the following files in the repository:

### Core Startup Logic
*   [`SchemaInitialization.java`](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/java/ac/uk/solent/com617/kuwaiba/osm_to_kuwaiba/startup/SchemaInitialization.java): Manages SQL script execution.
*   [`UprnDataInitializer.java`](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/java/ac/uk/solent/com617/kuwaiba/osm_to_kuwaiba/startup/UprnDataInitializer.java): Handles initial CSV data seeding.

### Database Definitions
*   [`schema.sql`](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/resources/schema.sql): Defines the raw database tables.
*   [`createtables.sql`](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/resources/createtables.sql): Contains the PostGIS cleaning and "Buildings as Points" logic.

### Models & Repositories
*   [`NetworkPoint.java`](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/java/ac/uk/solent/com617/kuwaiba/osm_to_kuwaiba/models/NetworkPoint.java): Includes the `osm_id` field for UPRN linking.
*   [`LinkedBuildingRepository.java`](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/java/ac/uk/solent/com617/kuwaiba/osm_to_kuwaiba/repository/LinkedBuildingRepository.java): Manages the query that sets UPRNs as building names.
