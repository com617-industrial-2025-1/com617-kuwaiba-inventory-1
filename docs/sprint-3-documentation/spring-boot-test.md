# Spring Boot Tests Documentation

## Overview

Testing plays an important role in this Spring Boot infrastructure prediction project by helping maintain reliable data processing, accurate geospatial calculations, and stable network hierarchy generation.

The test suite is built using:

- Spring Boot testing support with `@SpringBootTest`
- `@Transactional` annotations for isolated test execution
- `JdbcTemplate` for direct database verification
- JUnit 5 (Jupiter) for assertions and test execution
- PostGIS spatial functions for geometry-related operations

The tests cover both:

- Repository-level functionality (database operations)
- Service-level workflows (business logic and orchestration)

---

# OsmToKuwaibaApplicationTests

## Purpose

Ensures the Spring Boot application starts successfully and the application context loads without issues.

## What is being tested

### `contextLoads()`

- Confirms that the Spring Boot application context initializes correctly
- Acts as a basic smoke test for application startup

## Why these tests matter

Context loading tests help verify that the application configuration is valid before more advanced tests are executed.

They help identify issues such as:

- Missing bean definitions
- Circular dependencies
- Incorrect configuration properties
- Startup failures caused by invalid wiring

## Improvements

- The current test is lightweight but effective for startup validation
- Additional checks could verify important bean initialization such as database connectivity

## Additional Tests to Consider

- Configuration property validation
- Endpoint availability testing using `@WebMvcTest`

---

# BuildingDropPointTests

## Purpose

Tests the `BuildingDropPointRepository` and validates clustering logic between buildings and network poles.

## What is being tested

### `insertPoleClusters()`

- Verifies that poles are generated for grouped buildings
- Ensures only valid pole records are created

### `insertPoleClusters_doesNotCreatePolesWhenNoBuildingsExist()`

- Verifies the system handles empty datasets correctly

### `updateBuildingParents()`

- Ensures every building receives a parent pole assignment

### `updateBuildingParents_bdpParentIdLinksToPole()`

- Validates that assigned parent IDs reference existing pole records

## Why these tests matter

Building-to-pole relationships are a key part of the network prediction process.

Incorrect clustering or parent assignments could lead to:

- Broken network paths
- Unreachable buildings
- Invalid topology generation

These tests confirm that buildings are correctly grouped and linked before later stages of the prediction workflow run.

## Improvements

- Test data currently relies on hardcoded coordinates
- Parameterized testing could improve coverage for different cluster patterns
- Distance threshold validation can be expanded

## Additional Tests to Consider

- Boundary testing around the 68m clustering limit
- Large dataset performance testing
- Validation of different cluster sizes and densities

---

# LinkedBuildingRepositoryTests

## Purpose

Tests the `LinkedBuildingRepository` and validates UPRN linking operations and building retrieval functionality.

## What is being tested

### `findAll()`

- Retrieves all linked building records from the database

### `findByUprn()`

- Searches for buildings using a UPRN identifier

### `findByUprn_returnsEmptyListWhenUprnDoesNotExist()`

- Ensures non-existent UPRNs are handled gracefully

### `createLinkedBuildings()`

- Executes the linked building generation process

### `createLinkedBuildings_tableIsQueryableAfterCall()`

- Confirms the generated table remains accessible after execution

## Why these tests matter

UPRN linking connects OpenStreetMap building data with official UK addressing records.

Correct associations are important for:

- Accurate delivery predictions
- Address verification
- Reliable infrastructure mapping

## Improvements

- Current tests mainly verify successful execution
- Data accuracy validation could be expanded
- More realistic test datasets would improve reliability

## Additional Tests to Consider

- Address matching accuracy validation
- Data consistency checks
- Performance testing with large datasets

---

# NetworkConnectionTests

## Purpose

Tests the `NetworkConnectionRepository` and validates infrastructure connection generation.

## What is being tested

### `insertDropConnections()`

- Creates connections between buildings and poles

### `insertDropConnections_linksBuildingToParentPole()`

- Verifies valid building-to-pole relationships

### `insertFeederConnections()`

- Creates pole-to-cabinet connections

### `insertFeederConnections_linksPoletoParentCabinet()`

- Validates cabinet linkage

### `insertDistributionConnections()`

- Creates cabinet-to-aggregator connections

### `insertDistributionConnections_linksCabinetToAggregator()`

- Confirms hierarchy relationships are correctly generated

## Why these tests matter

Network connections define the routing structure of the infrastructure model.

Without valid connections:

- Signal paths cannot be traced
- Infrastructure mapping becomes unreliable
- Prediction accuracy decreases

## Improvements

- Geometry validation coverage could be improved
- Loop prevention checks are currently missing

## Additional Tests to Consider

- Validation of generated `LINESTRING` geometries
- Duplicate connection prevention
- Connection distance validation

---

# NetworkPointTests

## Purpose

Tests the `NetworkPointRepository` and validates hierarchical network node creation and management.

## What is being tested

### Retrieval Tests

- `findByType()`
- `findByParentId()`
- `findByTypeAndParentId()`

### Cluster Creation Tests

- `insertCabinetClusters()`
- `insertAggregatorClusters()`
- `insertExchangeClusters()`

### Parent Linking Tests

- `updatePoleParents()`
- `updateCabinetParents()`
- `updateAggregatorParents()`

## Why these tests matter

The network hierarchy is central to infrastructure prediction and routing.

Correct clustering supports:

- Efficient network organization
- Accurate routing
- Better capacity planning
- Reliable service delivery predictions

## Improvements

- Current tests assume fixed clustering behavior
- Spatial distribution validation could be improved

## Additional Tests to Consider

- Cluster boundary validation
- Scaling tests for large network structures
- Cluster balancing validation

---

# ClusteringServiceTests

## Purpose

Integration tests for the `ClusteringService` workflow responsible for generating the network hierarchy.

## What is being tested

### `runFullPrediction()`

- Executes the full clustering process from start to finish

### Workflow Validation

- Removes existing topology data
- Creates poles
- Creates cabinets
- Creates aggregators
- Creates exchanges
- Links hierarchy levels correctly

## Why these tests matter

The clustering service coordinates the complete topology generation workflow.

Integration testing helps identify:

- Workflow sequencing issues
- State management problems
- Service coordination failures

## Improvements

- Failure handling scenarios are limited
- Performance benchmarking is not included

## Additional Tests to Consider

- Real-world scale testing
- Recovery testing after failures
- Idempotency validation

---

# RoutingServiceTests

## Purpose

Integration tests for the `RoutingService` responsible for connection and routing prediction.

## What is being tested

### `runFullPrediction()`

- Executes the full routing workflow

### Connection Validation

- DROP connections
- FEEDER connections
- DISTRIBUTION connections
- TRUNK connections

## Why these tests matter

Routing determines how signals travel through the generated network.

Incorrect routing may result in:

- Invalid topology structures
- Unreachable infrastructure
- Incorrect serviceability predictions

## Improvements

- Loop detection tests are currently missing
- Large-scale performance validation could be improved

## Additional Tests to Consider

- Circular dependency detection
- Performance testing with large infrastructure datasets

---

# UprnServiceTests

## Purpose

Tests the `UprnService` responsible for linking buildings with UK national addressing records.

## What is being tested

### `linksUprns()`

- Executes the UPRN linking process

### `linksUprns_tableExistsAfterCall()`

- Verifies that the linked buildings table exists after execution

### `linked_buildings_tableExists()`

- Confirms the expected schema structure is available

## Why these tests matter

UPRN linking integrates OpenStreetMap data with authoritative UK addressing systems.

This is important for:

- Accurate infrastructure planning
- Service availability validation
- Reliable cross-system integration

## Improvements

- Validation depth is currently limited
- Failure scenarios are not heavily tested

## Additional Tests to Consider

- Coverage reporting and metrics
- Malformed data handling
- Large-scale performance testing
- Idempotency checks

---

# KuwaibaProvisioningModelTest

## Purpose

Tests serialization and deserialization behavior of `KuwaibaProvisioningRequisition` objects.

## What is being tested

### `test()`

- Serializes objects into JSON format
- Deserializes JSON back into Java objects
- Verifies Jackson `ObjectMapper` behavior
- Generates metadata template files

## Why these tests matter

Provisioning requisitions are used to define deployment instructions for Kuwaiba integrations.

Reliable serialization ensures:

- Safe import/export operations
- Stable integration with external services
- Prevention of data corruption during transfer

## Improvements

- More detailed field-level assertions could be added
- Edge case coverage is currently limited

## Additional Tests to Consider

- Data integrity validation
- Full field coverage checks
- Nested object validation
- Collection serialization checks

---

# Summary of Test Coverage

| Category          | Coverage               | Status |
| ----------------- | ---------------------- | ------ |
| Repository Tests  | 5 implemented          | 100%   |
| Service Tests     | 3 implemented          | 100%   |
| Model Tests       | 1 implemented          | 100%   |
| Integration Tests | 2 end-to-end workflows | 100%   |

---

# Key Strengths

- Strong integration-level test coverage
- Proper use of `@SpringBootTest` and `@Transactional`
- Reliable database validation with `JdbcTemplate`
- Consistent setup using `@BeforeEach`

---

# Areas for Improvement

- More edge case validation
- Better scalability and performance testing
- Increased negative test coverage
- Deeper serialization integrity validation
