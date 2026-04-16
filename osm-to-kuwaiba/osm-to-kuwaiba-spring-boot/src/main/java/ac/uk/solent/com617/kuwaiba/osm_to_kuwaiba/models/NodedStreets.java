package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

import org.locationtech.jts.geom.LineString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "noded_streets")
public class NodedStreets {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(columnDefinition = "geometry(LineString, 3857)")
	private LineString geom;
	
	@Column(name = "source")
	private Integer source;
	
	@Column(name = "target")
	private Integer target;
	
	@Column(name = "cost")
	private Double cost;
	
	
	// getters and setters
	public Long getId() { return id; }
	public LineString getGeom() { return geom; }
	public Integer getSource() { return source; }
	public Integer getTarget() { return target; }
	public Double getCost() { return cost; }
	
	public void setId(Long idIn) { id = idIn; }
	public void setGeom(LineString geomIn) { geom = geomIn; }
	public void setSource(Integer sourceIn) { source = sourceIn; }
	public void setTarget(Integer targetIn) { target = targetIn; }
	public void setCost(Double costIn) { cost = costIn; }
	
	
}
