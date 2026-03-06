package com.com617.spring.osm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.com617.spring.osm.models.Location;
import com.com617.spring.osm.repository.LocationRepository;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public List<Location> getLocationsNear(double longitude, double latitude, double distance) {
        return locationRepository.findWithinDistance(longitude, latitude, distance);
    }
}