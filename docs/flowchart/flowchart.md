# Flowchart


### Data Extraction

**OpenStreetMap Export** 
- [OpenStreetMap](https://www.openstreetmap.org/export) as `.osm` which is an XML format.
- [Humanitarian OpenStreetMap Team](export.hotosm.org) in multiple formats.
- [qgis] through the **quickosm** plugin.

**UPRN retrieval**
- [OS Data Hub](https://osdatahub.os.uk/data/downloads/open/OpenUPRN) as `.csv` or `.gpkg` Geopackage

### Data Processing

**Data Processing** involves matching UPRN coordinates to OpenStreetMap building infastructure
coordinates, general cleaning of the data and removal of unecessary data.

There are multiple options for which **format** we would like to have our data in...

**[GeoPackage](https://www.geopackage.org/)**
The `.gpkg` format can be used with [qgis](https://qgis.org/). Data could be formatted into
`.gpkg` by using **geopandas** python library. Offers multiple layers in one file (e.g. building
layer, streets layer, proposed infastructure layer)

**[GeoJSON](https://geojson.org/)**

**[PostGIS](https://postgis.net/)** Extends functionality of PostgreSQL database to include
geospatial data. [PGAdmin 4](pgadmin.org/) can be used to manage the database and may have a
"*geometric viewer*" to preview map data. [osm2pgsql](https://osm2pgsql.org/) 
and [ogr2ogr](https://gdal.org/en/stable/programs/ogr2ogr.html) may be useful tools for this.




