## Sprint 1 Document

## UPRN Research

- Sprint 1 Document Contents
- UPRN Research
- 1. Introduction
   - 1. What the project is about
   - 2. Why UPRN matters
- 2. What is a UPRN
- 3. Ordnance Survey Data
- 4. Methods of Retrieval
- 5. Data Conversion
- 6. Linking UPRN to OpenStreetMap
- 7. Challenges and risks
- 8. Conclusion


## 1. Introduction

### 1. What the project is about

This project aims to develop a system capable of extracting street layouts and residential
building data from OpenStreetMap (OSM) and using that information to design a proposed
telecommunications network infrastructure. The overall objective is to create an algorithm that
can automatically analyze an area, identify houses and streets, and suggest appropriate
locations for infrastructure elements such as fiber cables, telephone poles, street cabinets, and
manhole covers. The final output will be structured in a format suitable for export into Kuwaiba
for network inventory management.

A key requirement of the project is ensuring that each property can be uniquely identified. In
the UK, this is achieved through the Unique Property Reference Number (UPRN), which is
provided by Ordnance Survey datasets such as AddressBase. While OpenStreetMap provides
detailed geographic information about buildings and roads, it does not inherently include
UPRNs. Therefore, part of the project involves researching how UPRN data can be retrieved
from Ordnance Survey, understanding the format in which it is supplied, and determining how it
can be linked to OpenStreetMap building data.

This document focuses specifically on investigating the retrieval, structure, and potential
integration of UPRN data, forming a foundation for later stages of the project where automated
network planning and data export will be implemented.


### 2. Why UPRN matters

The Unique Property Reference Number (UPRN) plays a critical role in ensuring that each
property can be accurately and consistently identified within a dataset. Unlike an address, which
may change over time or be written in different formats, a UPRN is a permanent and unique
numerical identifier assigned to a specific location. This makes it significantly more reliable for
linking datasets and managing infrastructure information.

In the context of this project, UPRNs provide a stable method of identifying individual houses
when designing a telecommunications network. Since the aim is to analyze streets and
residential properties in order to suggest fiber infrastructure, it is essential that each building
can be uniquely referenced. Relying solely on building geometry or address text may introduce
ambiguity, particularly in cases such as apartment blocks, newly built properties, or areas where
address formatting varies.

UPRNs also enable integration between different data sources. For example, Ordnance Survey
datasets may contain authoritative property identifiers, while OpenStreetMap provides detailed
geographic building outlines. By linking these sources through spatial matching and attaching
the corresponding UPRN to each building, the system gains a consistent and traceable property
reference. This improves data integrity, reduces duplication, and supports future scalability of
the network planning system.

Furthermore, using UPRNs aligns the project with industry and government standards, as they
are widely adopted across utilities, local authorities, and telecommunications providers.
Incorporating UPRNs therefore strengthens the realism and practical applicability of the
proposed solution.


## 2. What is a UPRN

A Unique Property Reference Number (UPRN) is a permanent numerical identifier assigned to
every addressable property in the United Kingdom. It is issued as part of the national address
database and remains consistent throughout the lifecycle of a property, even if the address
itself changes. This means that while street names, postcodes, or property names may be
updated over time, the UPRN remains fixed.

Each UPRN uniquely identifies a specific geographic location, whether that is a residential
house, flat, commercial building, or other addressable structure. This eliminates ambiguity that
can arise from similar or duplicated addresses and ensures that each property can be
referenced precisely.

UPRNs are widely used across government departments, local authorities, utility providers, and
telecommunications companies. They serve as a standard reference point for managing
services, infrastructure, billing, planning, and regulatory processes.

**Why UPRNs Are Useful**

The use of UPRNs provides several important benefits:

**Prevents duplication:** Since each property has one unique identifier, datasets can avoid
repeated or conflicting entries for the same location.

**Enables data integration:** Different organizations may hold separate datasets (e.g., planning
data, utility records, telecom infrastructure). The UPRN acts as a common key that allows these
datasets to be linked accurately.

**Improves planning accuracy:** When designing infrastructure such as fiber networks, having a
unique identifier for each property ensures that service coverage, connections, and resource
allocation can be managed systematically and reliably.

In the context of project, incorporating UPRNs strengthens data consistency and supports
accurate modelling of telecommunications infrastructure at a property level.


## 3. Ordnance Survey Data

Ordnance survey has several products as below.

1. Addresses and names –
    Explore mapping data relating to addresses, UPRNs, named features, and places.
    Discover address details, postcodes, identifiers and features.
2. Maps and imagery –
    Explore mapping data designed to provide visual context and simple analysis. Discover
    raster maps, imagery and topography layers.
3. Transport networks –
    Explore mapping data designed to provide information about roads, paths, trams, ferries
    and railways. Discover networks, features and identifiers.
4. Buildings and infrastructure –
    Explore mapping data designed to provide information about the built environment.
    Discover building and structure features, identifiers and topography layers.
5. Land and terrain –
    Explore mapping data designed for land cover, land use and elevation. Discover features,
    greenspace and topography layers.
6. Water –
    Explore mapping data designed for water bodies, tidelines and river networks. Discover
    features, identifiers and topography layers.
7. Areas and zones –
    Explore mapping data designed to provide information about areas used for
    administration and analysis. Discover postcodes, boundaries and built up areas.
8. OS Net positioning data –
    Discover OS Net data, providing Global Navigation Satellite Systems (GNSS, previously
    referred to as GPS) data.


From above available services under Addresses and names service, The URPN can be
extracted from the below listed datasets (OS products).

1. OS GB Address –
    A complete and authoritative addressing dataset for Great Britain, providing a
    detailed view of an address and its lifecycle, based on pre-build, built, and
    historical property lifecycle phases.


What OS GB Address provides


UPRN data management –
Link data to an address using the Unique Property Reference Number (UPRN). This
identifier is assigned to a property and persists through its full lifecycle. From
construction through to its demolition, the UPRN ensures accurate and consistent
management of data, even when changes are made to an address.


Data Structure – Vector
Format – CSV, GeoPackage

2. AddressBase Core –
    AddressBase Core contains more than 33 million addresses. It includes Unique Property
    Reference Numbers (UPRNs), property-level coordinates and secondary classifications.
    This makes it suitable for analysis and understanding of property types, as well as uses
    that combines authoritative data from local authorities not found in Royal Mail Postal
    Address File.


What AddressBase Core provides

The dataset contains –
AddressBase Core contains more than 33 million addresses. It includes Unique Property
Reference Numbers (UPRNs), property-level coordinates and secondary classifications.

Data Structure – Vector
Format – CSV, GeoPackage


3. OS Open UPRN –
    OS Open UPRN provides a coordinates reference point and Unique Property Reference
    Number for every addressable location across Great Britain. A UPRN is an identifier used
    to identify an addressable location, it helps to reduce ambiguity and ensures accurate
    identification of locations.


What OS Open UPRN provides

Unique Property Reference Number –
A Unique Property Reference Number (UPRN) is a unique numeric identifier for every
addressable location in Great Britain, found in OS's AddressBase products.

Addressable features –
UPRNs are assigned to approximately 40 million addressable locations, which may be
any kind of building; residential or commercial. It may also be an object that might not
have a ‘postal ’ address – such as a bus shelter or electricity sub-station.

Share and link data –
OS Open UPRN will enable you to share and link data related to UPRNs, which you can
spatially analyse and visualise using the accurate location.

Authoritative source –
The UPRNs in OS Open UPRN are primarily allocated by Local Authorities, under their
legal duty to maintain this information. This means you can have confidence you're
accessing an authoritative source of these identifiers.

Identifiers you can trust –
The UPRN is a persistent identifier to an addressable location that is never re-used,
allowing you to confidently manage associated information to an address.

Data Structure – Vector
Format – CSV, GeoPackage


## 4. Methods of Retrieval

There are several ways this data can be obtained:

1. Direct Download (Licensed Access)

Licensed datasets such as OS GB Address and AddressBase Core can be accessed through formal
agreements with Ordnance Survey. This may involve institutional access via a university licence
or organisational subscription. These datasets are typically downloaded as bulk files in CSV or
GeoPackage format.

2. OS Data Hub API

Ordnance Survey provides access to certain datasets via its Data Hub platform, which allows
programmatic access through APIs. This method is suitable for automated workflows or
applications that require dynamic querying rather than full dataset downloads.

3. Bulk Dataset Download

Some datasets, particularly OS Open UPRN, can be downloaded as bulk vector files. This
approach is appropriate for large-scale processing where spatial matching and analysis will be
performed locally.

Licensing Considerations

It is important to note that not all Ordnance Survey datasets are fully open. While OS Open
UPRN is available under open data terms, products such as OS GB Address and AddressBase
Core require appropriate licensing. Usage restrictions may apply regarding redistribution,
commercial use, and data sharing.

For this project, licensing constraints must be considered when selecting a dataset. If the goal is
to demonstrate feasibility within an academic context, OS Open UPRN may be sufficient.
However, for more detailed lifecycle or classification analysis, a licensed dataset such as
AddressBase Core may be required.


## 5. Data Conversion

Ordnance Survey datasets are typically provided in structured vector formats such as CSV and
GeoPackage. While these formats are suitable for storage and spatial analysis, they may not
directly align with the formats required by the proposed system architecture. Therefore, data
conversion and preparation form an important step in the workflow.

**Input Formats**

The relevant datasets (such as OS Open UPRN, AddressBase Core, or OS GB Address) are
commonly delivered in:

- **CSV (Comma-Separated Values)** – Tabular format containing UPRN, coordinates, and
    associated attributes.
- **GeoPackage (GPKG)** – A spatial database format that stores vector geometries along
    with attribute data.

Both formats contain vector-based spatial data, meaning that each record represents a
geographic feature (e.g., a point location for a property).

**Required Output Formats**

Depending on the system design, the data may need to be converted into formats more suitable
for:

- Spatial analysis (e.g., GeoJSON)
- Database storage (e.g., PostGIS)
- Integration with OpenStreetMap-derived data
- Export to downstream systems such as Kuwaiba

For example, if spatial matching is performed in a GIS workflow, converting CSV data into a
spatial format (by assigning latitude and longitude as geometry) may be necessary. Similarly,
GeoPackage files may need to be transformed into GeoJSON for use within web-based mapping
tools.

**Conversion Tools and Methods**

Several established tools can support this process:


- **GDAL / ogr2ogr** – For converting between spatial formats (e.g., GeoPackage to
    GeoJSON).
- **QGIS** – For visual inspection, re-projection, and format conversion.
- **Python libraries (e.g., Pandas, GeoPandas)** – For programmatic processing and spatial
    operations.
- **Database systems (e.g., PostGIS)** – For storing and querying large datasets efficiently.

During conversion, it is important to ensure:

- Coordinate reference systems (CRS) are consistent between datasets.
- Geometry types are preserved correctly (e.g., points for UPRN locations).
- Attribute fields such as UPRN values remain intact and unmodified.

**Data Preparation for Linking**

Before linking Ordnance Survey data with OpenStreetMap building data, preprocessing may
include:

- Removing unnecessary attributes to reduce dataset size.
- Ensuring coordinate precision is consistent.
- Filtering to a defined geographic boundary (study area).
- Standardising projection systems to avoid spatial misalignment.

Proper data conversion and preparation ensure compatibility between datasets and improve
the reliability of the spatial matching process in later stages of the project.


## 6. Linking UPRN to OpenStreetMap

This section outlines how UPRN data obtained from Ordnance Survey can be linked to building
data extracted from OpenStreetMap (OSM). Establishing this connection is essential in order to
uniquely identify each property within the proposed telecom network model.

**The Problem**

Although both datasets describe real-world properties, they contain different types of
information:

- **OpenStreetMap** provides detailed building geometries (polygons) and street layouts but
    does not typically include UPRNs.
- **Ordnance Survey datasets** (such as OS Open UPRN or AddressBase products) provide
    authoritative UPRNs and property-level coordinates, but do not include full building
    footprint geometries in the same way as OSM.

As a result, the two datasets cannot be directly joined using a shared attribute key. A spatial
method of linking is therefore required.

**Proposed Solution: Spatial Matching**

The most appropriate approach is to match the datasets based on geographic location. Since
both datasets include coordinate information, a spatial proximity method can be used to
associate each UPRN point with the correct OSM building polygon.

**Proposed Method**

The linking process would follow these steps:

1. **Extract UPRN Coordinates**
    Retrieve the latitude and longitude values for each UPRN record from the Ordnance
    Survey dataset.
2. **Load OSM Building Data**
    Extract building polygons from OpenStreetMap for the same geographic area.
3. **Identify the Nearest Building Polygon**
    For each UPRN coordinate point, calculate the nearest OSM building polygon using
    spatial analysis tools.


4. **Apply a Distance Threshold**
    If the UPRN point lies within, or is within a defined threshold distance (e.g., 2–5 metres)
    of a building polygon, it can reasonably be assumed to correspond to that building.
5. **Attach UPRN as an Attribute**
    Once matched, the UPRN can be stored as an attribute of the corresponding OSM
    building record in the enhanced dataset.

**Considerations and Limitations**

This process is known as **spatial matching** or **proximity-based spatial joining**. While it is a logical
and widely used approach in GIS workflows, it is important to acknowledge certain limitations:

- Coordinate accuracy may vary between datasets.
- Some buildings may be missing or inaccurately mapped in OSM.
- Multi-dwelling buildings (e.g., apartment blocks) may contain multiple UPRNs for a
    single building polygon.
- Very dense urban environments may increase the risk of incorrect nearest-neighbour
    matches.

For the purposes of this project, this method is considered acceptable as a prototype-level
solution. The objective is to demonstrate technical feasibility and logical integration between
datasets rather than to achieve production-grade precision.

By implementing spatial matching, the project can successfully combine authoritative property
identifiers with detailed building geometry, forming a unified dataset suitable for telecom
network planning and export into systems such as Kuwaiba.


## 7. Challenges and risks

While the proposed approach is technically feasible, several challenges and risks must be
considered when integrating Ordnance Survey UPRN data with OpenStreetMap building data.
Identifying these early helps ensure that appropriate mitigation strategies can be planned in
later development stages.

**Coordinate Accuracy Differences**

Although both datasets contain geographic coordinates, they may differ in precision and
positional accuracy. Ordnance Survey data is authoritative and typically highly accurate,
whereas OpenStreetMap data is community-generated and may vary depending on the area.
Small positional discrepancies could result in incorrect spatial matches if tolerance thresholds
are not carefully defined.

**Missing or Incomplete Buildings in OSM**

OpenStreetMap coverage varies geographically. In some areas, building footprints may be
incomplete, outdated, or entirely missing. This could prevent certain UPRNs from being
successfully matched to a corresponding building polygon, reducing overall completeness of the
linked dataset.

**Licensing Restrictions**

Not all Ordnance Survey datasets are open. Products such as AddressBase Core and OS GB
Address require appropriate licensing agreements. Usage restrictions may limit redistribution or
integration with openly shared datasets. Careful consideration must be given to licensing terms
to ensure compliance, particularly if the project were to move beyond an academic prototype.

**Multiple UPRNs per Building**

Certain properties, such as apartment blocks or subdivided commercial units, may contain
multiple UPRNs within a single building footprint. Since OpenStreetMap typically represents
such properties as a single polygon, additional logic may be required to manage one-to-many
relationships between UPRNs and buildings.

**Performance and Scalability**

Handling large datasets—potentially tens of millions of address records—may introduce
performance challenges. Spatial matching operations can be computationally intensive,
particularly when processing large geographic areas. Efficient indexing, spatial databases, or
batching strategies may be required if the system is scaled beyond a small study area.


## 8. Conclusion

This research demonstrates that UPRN data can be reliably retrieved from Ordnance Survey
datasets such as OS Open UPRN and AddressBase products. The data is available in structured
vector formats, including CSV and GeoPackage, making it suitable for processing and integration
within a GIS-based workflow.

The datasets can be converted and standardised where necessary to ensure compatibility with
OpenStreetMap building data. Through spatial matching techniques, UPRN coordinate points
can be linked to corresponding OSM building polygons, creating a unified dataset that combines
authoritative property identifiers with detailed spatial geometry.

This integration provides a strong foundation for automated telecom network planning. By
uniquely identifying each property within the study area, the system can support accurate
infrastructure design and enable structured export into network inventory platforms such as
Kuwaiba.



