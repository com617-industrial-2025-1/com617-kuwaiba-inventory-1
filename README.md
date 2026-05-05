# COM617 Kuwaiba Network Inventory

A university industrial consulting project (COM617) at Solent University. The project produces
an automated pipeline that predicts fibre telecommunications infrastructure from OpenStreetMap data
and exports the results into Kuwaiba, an open-source network inventory management system.

## What It Does

Starting from an OpenStreetMap export (.xml, .pbf, .osm.pbf) and an optional UPRN dataset, the 
system:
- Imports and cleans building footprints and road geometry from OSM
- Predicts a four level fibre network hierarchy (poles, cabinets, aggregators and exchanges) using
  PostGIS spatial clustering
- Traces road following cable connections between each level using pgRouting
- Exposes the results via a REST API with GeoJSON output for use in QGIS or Kuwaiba

## Getting Started

See the [osm-to-kuwaiba](./osm-to-kuwaiba/) directory for setup and user instructions. Docker 
Desktop is the only prerequisite.

## Repository Structure

```
com617-kuwaiba-inventory-1/
├── osm-to-kuwaiba/               # Main application (Docker Compose stack)
│   ├── osm-to-kuwaiba-spring-boot/   # Spring Boot REST API
│   ├── container-fs/                 # Data, config, and init files for Docker services
│   └── docker-compose.yml
├── docs/                         # Project documentation
│   ├── sprint-3-documentation/   # Technical write-ups for each component
│   ├── infrastructure-prediction/
│   ├── planning-and-research/
│   └── ...
└── workup/                       # Experimental and reference work (not part of main project)
```

## Documentation

Technical Documentation is in the [docs](./docs/) folder, covering data extraction, data processing,
infrastructure prediction and sprint component write ups.


## Project Google Document

The report document for university assessment can be found [here](https://docs.google.com/document/d/1keXioDJD0lFwCEk_XmnK4_YQjVj3Teg2Vi0qG5qHSBw/edit?usp=sharing)


## Project Information 

| | |
|---|---|
| Module | COM617 Industrial Consulting Project |
| University | Solent University |
| Tutor | Craig Gallen |
| Participants | Rachel Ayres, Hayden Calkin, Corey Maltby, Oliver Myers, Oskar Phung Van, Hasnain, Ravidu |
