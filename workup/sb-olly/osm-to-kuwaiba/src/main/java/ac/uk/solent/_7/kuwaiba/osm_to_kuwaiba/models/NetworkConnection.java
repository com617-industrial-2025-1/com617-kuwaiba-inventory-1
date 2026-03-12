package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.LineString;

@Entity
@Table(name = "network_connection")
public class NetworkConnection {
    @Id
    private Long id;
    private String external_id;
    private Long start_id;
    private Long end_id;
    private String link_type;

    // Using SRID 3857
    @Column(columnDefinition = "geometry(LineString, 3857)")
    private LineString geom;

    // getters and setters
    public Long getId() { return id; }
    public String getExternal_id() { return external_id; }
    public Long getStart_id() { return start_id; }
    public Long getEnd_id() { return end_id; }

    @JsonIgnore
    public LineString getGeom() { return geom; }

    public void setId(Long id) { this.id = id; }
    public void setExternal_id(String external_id) { this.external_id = external_id; }
    public void setStart_id(Long id) { this.start_id = id; }
    public void setEnd_id(Long id) { this.end_id = id; }
    public void setGeom(LineString geom) { this.geom = geom; }

}
