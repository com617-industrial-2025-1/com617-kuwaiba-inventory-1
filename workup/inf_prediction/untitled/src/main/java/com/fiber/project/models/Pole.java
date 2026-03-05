package com.fiber.project.models;

import org.locationtech.jts.geom.Point;
import java.util.ArrayList;
import java.util.List;

public class Pole {
    private long id;
    private Point location;
    private long cabinetId;
    private List<Long> servedUprns = new ArrayList<>();

    public Pole(long id_, Point location_) {
        this.id = id_;
        this.location = location_;
    }

    public long getId() { return id; }
    public Point getLocation() { return location; }
    public long getCabinetId() { return cabinetId; }
    public List<Long> getServedUprns() { return servedUprns; }
}
