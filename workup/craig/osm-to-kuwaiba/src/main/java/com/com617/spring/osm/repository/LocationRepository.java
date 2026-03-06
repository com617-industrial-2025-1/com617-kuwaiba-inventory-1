package com.com617.spring.osm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.com617.spring.osm.models.Location;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
   
   /**
    * Find locations within a certain distance (in meters) from a given longitude and latitude.
    * The custom query above uses PostGIS’s ST_DWithin function to find locations within a certain distance from a given point, 
    * which is particularly useful for proximity searches.
    * @param longitude
    * @param latitude
    * @param distanceInMeters
    * @return
    */
    @Query(value = "SELECT * FROM Location WHERE ST_DWithin(location, ST_SetSRID(ST_MakePoint(?1, ?2), 4326), ?3)", nativeQuery = true)
    List<Location> findWithinDistance(double longitude, double latitude, double distanceInMeters);
    
}