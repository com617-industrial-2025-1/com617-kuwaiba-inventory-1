#!/bin/bash

set -e

echo "--- Starting OSM Data Migration ---"

# Running osm2pgsql to import raw data into postgis.
# $POSTGRES_DB and $POSTGRES_USER are provided by docker-compose environment variable.
osm2pgsql -c -d "$POSTGRES_DB" -U "$POSTGRES_USER" /data/southampton.pbf

echo "--- OSM Data Migration Complete ---"
