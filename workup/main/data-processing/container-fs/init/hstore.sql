-- switches the connection to the osm database before creating the extensions
\c osm
-- using init removed the postgis own init script so we add the postgis extensions here as well.
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
CREATE EXTENSION IF NOT EXISTS hstore;