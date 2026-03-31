package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "building_drop_points")
public class BuildingDropPoint {
    @Id
    @Column(name = "building_id")
    private Long buildingId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(columnDefinition = "geometry(Point, 3857)")
    private Point geom;

    // getters and setters
    public Long getBuildingId() { return buildingId; }
    public Long getParentId() { return parentId; }
    
    @JsonIgnore
    public Point getGeom() { return geom; }
    
    public void setBuildingId(Long id) { buildingId = id; }
    public void setParentId(Long id) { parentId = id; }
    public void setGeom(Point new_geom) { geom = new_geom; }
    
}
