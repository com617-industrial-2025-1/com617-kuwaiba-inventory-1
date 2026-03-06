package com.com617.spring.osm.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.com617.spring.osm.models.Location;
import com.com617.spring.osm.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // Southampton 
    // http://localhost:8080/api/locations/near?longitude=50.9105&latitude=1.4049&distance=400
    @GetMapping("/near")
    public List<Location> getLocationsNear(@RequestParam double longitude, @RequestParam double latitude, @RequestParam double distance) {
        return locationService.getLocationsNear(longitude, latitude, distance);
    }
}