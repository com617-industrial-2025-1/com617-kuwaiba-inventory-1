# Services and Orchestration Documentation
Relevant files are in the [services folder](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/java/ac/uk/solent/com617/kuwaiba/osm_to_kuwaiba/services/)

Initial Notes:

Document:
- The purpose of the service layer.
- How the service layer orchestrates which repository functions are called.

Note: There are services for the prediciton (routing and clustering) as well as a service for the uprn enrichment


The service layer is used to connect the API layer which handles HTTP requests and the repository layer which interacts with the database. Each service decides which SQL queries to run and in what order. Services were split into 4 sections with each one doing a different job:

UprnImportService - loads the raw UPRN data from a CSV file into the database.
UprnService - links the UPRNs to the cleaned buildings.
ClusteringService - predicts where the poles and cabinets etc should go.
RoutingService - predicts the cable connections between.
ClusteringService works bottom-up because each tier depends on the one below it: drop points into poles, poles into cabinets, cabinets into aggregators, and aggregators into exchanges. We clear old predictions first, deleting connections before points to avoid foreign key issues. RoutingService runs afterwards and works top-down, from exchange down to building, because that mirrors how a real network is laid out.

To eliminate the data being accidently loaded twice, data was checked in UprnImportService to see if it was already present before loading. If there is already data then the loading the data skips

