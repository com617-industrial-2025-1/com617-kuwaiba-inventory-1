package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "network_points")
public class NetworkPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String external_id;
    private Long parent_id;

    // Tells JPA to store the enum string in the database instead of number.
    @Enumerated(EnumType.STRING)
    private PointType type;

    // using SRID 3857
    @Column(columnDefinition = "geometry(Point, 3857)")
    private Point geom;

    // getters and setters
    public Long getId() { return id; }
    public String getExternal_id() { return external_id; }
    public Long getParent_id() { return parent_id; }
    public PointType getType() { return type; }

    @JsonIgnore
    public Point getGeom() { return geom; }

    public void setId(Long id) { this.id = id; }
    public void setExternal_id(String ex_id) { this.external_id = ex_id; }
    public void setParent_id(Long id) { this.parent_id = id; }
    public void setType(PointType type) { this.type = type; }
    public void setGeom(Point geom) { this.geom = geom; }
}
