package com.com617.spring.osm.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;

// see https://www.antanaskovic.com/en/blog/building-location-based-rest-api-with-osm-postgis-and-spring-boot
@Entity
@Table(name = "planet_osm_point")
public class OsmPoint {
   
  @Id
  private Long osm_id;

  private String name;

  // We ignore the 'way' field in JSON serialization to avoid issues with JTS geometry types
  @Column(columnDefinition = "geometry(Point,4326)")
  private Point way;

  public Long getOsm_id() {
   return osm_id;
  }

  public void setOsm_id(Long osm_id) {
   this.osm_id = osm_id;
  }

  public String getName() {
   return name;
  }

  public void setName(String name) {
   this.name = name;
  }

  // We ignore the 'way' field in JSON serialization to avoid issues with JTS geometry types
  @JsonIgnore
  public Point getWay() {
   return way;
  }

  public void setWay(Point way) {
   this.way = way;
  }

  
}