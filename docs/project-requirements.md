# Project Requirements

## 1 Project Objectives

### 1.1 Geographic Data Extraction
* **1.1.1 OpenStreetMap (OSM) Extraction:** The system must allow the user to select a specific area and then extract the street graphs and house locations.
* **1.1.2 UPRN Retrieval:** The UPRN for each house within the specific area must be retrieved and then linked to the house number based on its latitude and longitude.

### 1.2 Infrastructure Design and Prediction
* **1.2.1 Infrastructure Rules:** The user should be able to define the specific rules for their network infrastructure (e.g., how many houses to poles, how many poles to cabinets, and the distance between manhole covers).
* **1.2.2 Automated Placement:** The system should then suggest where each of these artefacts should be placed based on the rules provided.
* **1.2.3 Network Topology:** The system should record the physical path of the cables required, along with the amount of other artefacts.

### 1.3 Inventory Modelling
* **1.3.1 Data Formatting:** The locations of each artefact should be recorded based on the format required for Kuwaiba, along with a list of the number of items needed.
* **1.3.2 Data Export:** The formatted data should then be exported into Kuwaiba.

## 1.4 Simulation and Visualisation
* **1.4.1 Network Simulation:** Simulate the suggested network within Kuwaiba.
* **1.4.2 OSM Map:** Produce a map in OSM showcasing the predicted infrastructure.
