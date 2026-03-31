package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.MultiPolygon;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cleaned_buildings")
public class CleanedBuilding {
    @Id
    @Column(name = "osm_id")
    private Long osmId;

    @Column(columnDefinition = "geometry(MultiPolygon, 3857)")
    private MultiPolygon geom;

    @Column(name = "building_name")
    private String buildingName;
    
    @Column(name = "house_num")
    private String houseNum;
    
    @Column(name = "street_name")
    private String streetName;
    
    @Column(name = "floors")
    private String floors;

    // getters and setters
    public Long getOsmId() { return osmId; }
    
    @JsonIgnore
    public MultiPolygon getGeom() { return geom; }
    
    public String getBuildingName() { return buildingName; }
    public String getHouseNum() { return houseNum; }
    public String getStreetName() { return streetName; }
    public String getFloors() { return floors; }
    
    public void setOsmId(Long id) { osmId = id; }
    public void setGeom(MultiPolygon new_geom) { geom = new_geom; }
    public void setBuildingName(String name) { buildingName = name; }
    public void setHouseNum(String num) { houseNum = num; }
    public void setStreetName(String name) { streetName = name; }
    public void setFloors(String new_floors) { floors = new_floors; }
}
