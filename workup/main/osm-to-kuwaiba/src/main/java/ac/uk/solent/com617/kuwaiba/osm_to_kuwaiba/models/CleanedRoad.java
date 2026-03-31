package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.LineString;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cleaned_roads")
public class CleanedRoad {
    @Id
    @Column(name = "osm_id")
    private Long osmId;

    // TODO: find out road_type data type (String/Boolean)
    @Column(name = "road_type")
    private String roadType;
    
    @Column(name = "street_name")
    private String streetName;

    @Column(columnDefinition = "geometry(LineString, 3857)")
    private LineString geom;

    // getters and setters
    public Long getId() { return osmId; }
    public String getRoadType() { return roadType; }
    public String getStreetName() { return streetName; }
    
    @JsonIgnore
    public LineString getGeom() { return geom; }
    
    public void setId(Long new_id) { osmId = new_id; }
    public void setRoadType(String type) { roadType = type; }
    public void setStreetName(String name) { streetName = name; }
    public void setGeom(LineString new_geom) { geom = new_geom; }
    
    
}
