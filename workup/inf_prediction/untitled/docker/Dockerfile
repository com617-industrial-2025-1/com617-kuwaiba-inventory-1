FROM postgis/postgis:15-3.3

# Installing the conversion tool inside the container.
RUN apt-get update && apt-get install -y osm2pgsql postgresql-15-pgrouting

# Copying the test file into the container.
RUN mkdir -p /data
# This can probably be edited to an environment variable in the future.
COPY southampton.pbf /data/southampton.pbf

# Copying the SQL and Shell scripts into the AUTO-INIT folder
# We use numbers to ensure the schema runs before the data import.
COPY db_schema.sql /docker-entrypoint-initdb.d/01_schema.sql
COPY init-osm.sh /docker-entrypoint-initdb.d/02_init-osm.sh

# Make sure the script has permission to run.
RUN chmod +x /docker-entrypoint-initdb.d/02_init-osm.sh

