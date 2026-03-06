package com.com617.spring.osm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.com617.spring.osm.models.OsmPoint;

@Repository
public interface OsmPointRepository  extends JpaRepository<OsmPoint, Long> {
   
   @Query(value="SELECT * FROM planet_osm_point p WHERE ST_DWithin(p.way, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :distance) LIMIT 10", nativeQuery = true)
   List<OsmPoint> findNearby(@Param("longitude") double longitude,
            @Param("latitude") double latitude, 
            @Param("distance") double distance);
   
   @Query(value="SELECT * FROM planet_osm_point  LIMIT 10", nativeQuery = true)
   List<OsmPoint> findAll();

}
   