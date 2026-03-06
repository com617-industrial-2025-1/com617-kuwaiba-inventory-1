package com.com617.spring.osm.models;

// note javax replaced by jakarta
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.locationtech.jts.geom.Point;

@Entity
public class Location {
    @Id
    private Long id;

    // @Type(type = "jts_geometry") // not needed with latest Spring Data JPA and Hibernate Spatial
    private Point location;  // JTS geometry type for storing location coordinates

    // Standard getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
}