-- table creation is now handled by hibernate but this can be used as reference for what the tables should be
-- This needs to be run for the index creation.

CREATE TABLE IF NOT EXISTS cleaned_buildings (
	osm_id 			BIGINT PRIMARY KEY,
	geom			geometry(MultiPolygon, 3857),
	building_name	TEXT,
	house_num		TEXT,
	street_name		TEXT,
	floors			TEXT
);

CREATE INDEX IF NOT EXISTS idx_buildings_geom ON cleaned_buildings USING GIST (geom);

CREATE TABLE IF NOT EXISTS cleaned_roads (
	osm_id			BIGINT PRIMARY KEY,
	geom			geometry(LineString, 3857),
	road_type		TEXT,
	street_name		TEXT,
	is_island		BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_roads_geom ON cleaned_roads USING GIST (geom);

CREATE TABLE IF NOT EXISTS noded_streets (
	id				BIGSERIAL PRIMARY KEY,
    geom 			geometry(LineString, 3857),
    source			INTEGER,
    target			INTEGER,
    cost			DOUBLE PRECISION
);

CREATE INDEX IF NOT EXISTS idx_noded_streets_geom ON noded_streets USING GIST (geom);

CREATE TABLE IF NOT EXISTS building_drop_points (
	building_id		BIGINT PRIMARY KEY,
	parent_id		BIGINT,
	geom			geometry(Point, 3857)
);

CREATE INDEX IF NOT EXISTS idx_building_drop_points_geom ON building_drop_points USING GIST (geom);

CREATE TABLE IF NOT EXISTS network_points (
    id          	BIGSERIAL PRIMARY KEY,
    external_id 	TEXT,
    parent_id   	BIGINT,
    type        	TEXT,
    geom        	geometry(Point, 3857)
);

CREATE INDEX IF NOT EXISTS idx_network_points_geom ON network_points USING GIST (geom);
CREATE INDEX IF NOT EXISTS idx_network_points_type ON network_points (type);

CREATE TABLE IF NOT EXISTS network_connections (
    id          	BIGSERIAL PRIMARY KEY,
    external_id 	TEXT,
    start_id    	BIGINT,
    end_id      	BIGINT,
    link_type   	TEXT,
    geom        	geometry(LineString, 3857)
);

CREATE INDEX IF NOT EXISTS idx_network_connections_geom ON network_connections USING GIST (geom);

CREATE TABLE IF NOT EXISTS raw_uprns (
    uprn 			BIGINT,
    lat  			DOUBLE PRECISION,
    lon  			DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS linked_buildings (
    osm_id        	BIGINT PRIMARY KEY,
    building_name 	TEXT,
    house_num     	TEXT,
    street_name   	TEXT,
    floors        	TEXT,
    uprn          	BIGINT
);
