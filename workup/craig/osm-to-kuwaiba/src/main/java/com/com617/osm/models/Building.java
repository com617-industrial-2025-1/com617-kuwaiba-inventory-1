package com.com617.osm.models;

import org.locationtech.jts.geom.Point;

public class Building {
    private long uprn;
    private Point location;
    private Long poleId;

    public Building(long uprn_, Point location_) {
        this.uprn = uprn_;
        this.location = location_;
    }

    public long getUprn() { return uprn; }
    public Point getLocation() { return location; }
    public Long getPoleId() { return poleId; }
    public void setPoleId(Long poleId_) { this.poleId = poleId_; }
}
