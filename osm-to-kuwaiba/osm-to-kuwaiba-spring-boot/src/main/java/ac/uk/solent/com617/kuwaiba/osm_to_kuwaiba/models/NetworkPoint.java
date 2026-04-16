package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.PointSerializer;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "network_points")
public class NetworkPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "external_id", columnDefinition = "TEXT")
    private String externalId;
    
    @Column(name = "parent_id")
    private Long parentId;

    // Tells JPA to store the enum string in the database instead of number.
    @Enumerated(EnumType.STRING)
    private PointType type;

    // using SRID 3857
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(columnDefinition = "geometry(Point, 3857)")
    private Point geom;

    // getters and setters
    public Long getId() { return id; }
    public String getExternalId() { return externalId; }
    public Long getParentId() { return parentId; }
    public PointType getType() { return type; }

    public Point getGeom() { return geom; }

    public void setId(Long id) { this.id = id; }
    public void setExternalId(String ex_id) { this.externalId = ex_id; }
    public void setParentId(Long id) { this.parentId = id; }
    public void setType(PointType type) { this.type = type; }
    public void setGeom(Point geom) { this.geom = geom; }
}
