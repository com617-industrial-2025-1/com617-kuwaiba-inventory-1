package com.com617.spring.osm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.com617.spring.osm.models.OsmPoint;
import com.com617.spring.osm.repository.OsmPointRepository;

@Service
public class OsmPointService {

   @Autowired
   private OsmPointRepository repository;

   public List<OsmPoint> findNearby(double latitude, double longitude, double distance) {

      return repository.findNearby(longitude, latitude, distance);

   }

   public List<OsmPoint> findAll() {

      return repository.findAll();

   }
}