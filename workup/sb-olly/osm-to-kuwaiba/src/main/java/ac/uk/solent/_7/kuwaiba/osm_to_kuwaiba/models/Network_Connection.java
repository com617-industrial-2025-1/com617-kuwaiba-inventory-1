package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.locationtech.jts.geom.LineString;

@Entity
public class Network_Connection {
    @Id
    private long id;
    private String external_id;
    private long start_id;
    private long end_id;
    private String link_type;

    @JsonIgnore
    @Column(columnDefinition = "geometry(LineString, 4326)")
    private LineString geom;

    // getters and setters
    public long getId() { return id; }
    public String getExternal_id() { return external_id; }
    public long getStart_id() { return start_id; }
    public long getEnd_id() { return end_id; }
    public LineString getGeom() { return geom; }

    public void setId(long id) { this.id = id; }
    public void setExternal_id(String external_id) { this.external_id = external_id; }
    public void setStart_id(long id) { this.start_id = id; }
    public void setEnd_id(long id) { this.end_id = id; }
    public void setGeom(LineString geom) { this.geom = geom; }

}
