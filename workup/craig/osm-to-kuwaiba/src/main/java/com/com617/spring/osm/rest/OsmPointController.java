package com.com617.spring.osm.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.com617.spring.osm.models.OsmPoint;
import com.com617.spring.osm.service.OsmPointService;

@RestController
@RequestMapping("/api/points")
public class OsmPointController {


     @Autowired
     private OsmPointService osmPointService;
     
     // http://localhost:8080/api/points/near?latitude=1.4049&longitude=-50.9105&distance=500
     @GetMapping("/near")
     public List<OsmPoint> getNearby(@RequestParam double latitude, @RequestParam double longitude, @RequestParam(defaultValue = "500") double distance) {
        
       return osmPointService.findNearby(longitude, latitude, distance);
       
     }
     

     // http://localhost:8080/api/points/all
     @GetMapping("/all")
     public List<OsmPoint> getAll() {
        
       return osmPointService.findAll();
       
     }
   }