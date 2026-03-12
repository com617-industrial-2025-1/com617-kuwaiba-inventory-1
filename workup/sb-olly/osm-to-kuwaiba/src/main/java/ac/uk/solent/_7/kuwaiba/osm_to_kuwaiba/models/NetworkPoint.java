package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "network_point")
public class NetworkPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String external_id;
    private String type;

    // using SRID 3857
    @Column(columnDefinition = "geometry(Point, 3857)")
    private Point geom;

    // getters and setters
    public long getId() { return id; }
    public String getExternal_id() { return external_id; }
    public String getType() { return type; }

    @JsonIgnore
    public Point getGeom() { return geom; }

    public void setId(long id) { this.id = id; }
    public void setExternal_id(String ex_id) { this.external_id = ex_id; }
    public void setType(String type) { this.type = type; }
    public void setGeom(Point geom) { this.geom = geom; }
}
