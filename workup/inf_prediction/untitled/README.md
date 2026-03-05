# Southampton Fiber Mapping

## Prerequisites
- Docker & Docker Compose (For the PostGIS database)
- Java 17+ (OpenJDK recommended)
- IntelliJ IDEA (Recommended IDE)
- PostGIS-ready PBF file (e.g. `southampton.pbf` in `./docker`)

## Setup Instructions
**Environment Configuration**

You **must** create a `.env` file in the root directory (this file is ignored by Git for security).
It should look like the following:
```
DB_USER=admin
DB_USER=your_secure_password
DB_NAME=southampton_fiber
```
**Database Initialisation**

This project uses a custom Docker image that automatically imports OSM data on the first run.
```bash
docker-compose up --build -d
```
*Note: The first startup will take ~1-2 minutes*

Further Docker commands needed are:
```bash
# Force Rebuild
docker-compose up --build
# Starting up
docker-compose up -d
# Shutting down but keeping data
docker-compose down
# Shutting down and deleting the volume 
docker-compose down -v
```

**IDE Setup**

Edit the Run Configuration so that `DB_USER`, `DB_PASSWORD` and `DB_NAME` are also added to the
Run Config as environment variables.

## Project Architecture
- **Database**: PostGIS (PostgreSQL)
- **Import Tool**: `osm2pgsql`
- **Backend**: Java with HikariCP for connection pooling.


