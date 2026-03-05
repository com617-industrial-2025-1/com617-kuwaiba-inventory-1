-- This db schema is to set up the database for the prediction to take place so.

-- Extensions--
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pgrouting;

-- Dropping Tables (for resetting)--

DROP TABLE IF EXISTS fiber_links CASCADE;
DROP TABLE IF EXISTS buildings CASCADE;
DROP TABLE IF EXISTS poles CASCADE;
DROP TABLE IF EXISTS cabinets CASCADE;
DROP TABLE IF EXISTS aggregators CASCADE;
DROP TABLE IF EXISTS exchanges CASCADE;

-- Creating Tables --
-- "GEOMETRY(Point, 4326)" is telling the db that coordinates are being entered.

CREATE TABLE exchanges (
    id SERIAL PRIMARY KEY,
    external_id VARCHAR(50) UNIQUE NOT NULL,
    geom GEOMETRY(Point, 4326) NOT NULL
);

CREATE TABLE aggregators (
    id SERIAL PRIMARY KEY,
    external_id VARCHAR(50) UNIQUE NOT NULL,
    parent_id INT REFERENCES exchanges(id),
    geom GEOMETRY(Point, 4326) NOT NULL
);

CREATE TABLE cabinets (
    id SERIAL PRIMARY KEY,
    external_id VARCHAR(50) UNIQUE NOT NULL,
    parent_id INT REFERENCES aggregators (id),
    geom GEOMETRY(Point, 4326) NOT NULL
);

CREATE TABLE poles (
    id SERIAL PRIMARY KEY,
    external_id VARCHAR(50) UNIQUE NOT NULL,
    parent_id INT REFERENCES cabinets(id),
    geom GEOMETRY(Point, 4326) NOT NULL
);

CREATE TABLE IF NOT EXISTS buildings (
    uprn BIGINT PRIMARY KEY,
    parent_id INT REFERENCES poles(id),
    geom GEOMETRY(Point, 4326) NOT NULL
);

CREATE TABLE fiber_links (
    id SERIAL PRIMARY KEY,
    external_id VARCHAR(100) UNIQUE NOT NULL,
    start_id INT,
    end_id INT,
    link_type VARCHAR(20) CHECK (link_type IN ('SPINE', 'LATERAL', 'DROP')),
    geom GEOMETRY(LineString, 4326) NOT NULL
);

-- Spatial Indexes --
CREATE INDEX idx_buildings_geom ON buildings USING GIST (geom);
CREATE INDEX idx_poles_geom ON poles USING GIST (geom);
CREATE INDEX idx_links_geom ON fiber_links USING GIST (geom);