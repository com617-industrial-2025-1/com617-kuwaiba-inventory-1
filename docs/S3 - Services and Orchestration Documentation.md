The service layer is used to connect the API layer which handles HTTP requests and the repository layer which interacts with the database. It handles the logic that decides what needs to happen when a request comes in.
Services were split into 4 sections with each one doing a different job:

·	UprnImportService - loads the raw UPRN data from a CSV file into the database.
·	UprnService - links the UPRNs to the cleaned buildings.
·	ClusteringService - predicts where the poles and cabinets etc should go.
·	RoutingService  - predicts the cable connections between.

ClusteringService works bottom-up because each tier depends on the one below it: drop points into poles, poles into cabinets, cabinets into aggregators, and aggregators into exchanges. We clear old predictions first, deleting connections before points to avoid foreign key issues.
RoutingService runs afterwards and works top-down, from exchange down to building, because that mirrors how a real network is laid out.

To eliminate the data being accidently loaded twice, it is checked to see if it present before loading. If there is already data then this step is skipped. 
