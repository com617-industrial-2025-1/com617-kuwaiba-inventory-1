package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "linked_buildings")
public class LinkedBuilding {
	@Id
	@Column(name = "osm_id")
	private Long osmId;
	
	@Column(name = "building_name", columnDefinition = "TEXT")
	private String buildingName;
	
	@Column(name = "house_num", columnDefinition = "TEXT")
	private String houseNum;
	
	@Column(name = "street_name", columnDefinition = "TEXT")
	private String streetName;
	
	@Column(name = "floors", columnDefinition = "TEXT")
	private String floors;
	
	@Column(name = "uprn")
	private Long uprn;
	
	@Column(name = "lat")
	private Double lat;
	
	@Column(name = "lon")
	private Double lon;
	
	// getters and setters
	public Long getOsmId() { return osmId; }
	public String getBuildingName() { return buildingName; }
	public String getHouseNum() { return houseNum; }
	public String getStreetName() { return streetName; }
	public String getFloors() { return floors; }
	public Long getUprn() { return uprn; }
	public Double getLat() { return lat; }
	public Double getLon() { return lon; }
	
	public void setOsmId(Long id) { osmId = id; }
	public void setBuildingName(String name) { buildingName = name; }
	public void setHouseNum(String num) { houseNum = num; }
	public void setStreetName(String name) { streetName = name; }
	public void setFloors(String floors_) { floors = floors_; }
	public void setUprn(Long input) { uprn = input; }
	public void setLat(Double lat_) { lat = lat_; }
	public void setLon(Double lon_) { lon = lon_; }
	
	
}