package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.locationtech.jts.geom.LineString;

@Entity
@Table(name = "network_connections")
public class NetworkConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "external_id")
    private String externalId;
    
    @Column(name = "start_id")
    private Long startId;
    
    @Column(name = "end_id")
    private Long endId;

    // Tells JPA to store the enum string in the database instead of number.
    @Enumerated(EnumType.STRING)
    @Column(name = "link_type")
    private LinkType linkType;

    // Using SRID 3857
    @Column(columnDefinition = "geometry(LineString, 3857)")
    private LineString geom;

    // getters and setters
    public Long getId() { return id; }
    public String getExternal_id() { return externalId; }
    public Long getStart_id() { return startId; }
    public Long getEnd_id() { return endId; }
    public LinkType getLink_type() { return linkType; }

    @JsonIgnore
    public LineString getGeom() { return geom; }

    public void setId(Long id) { this.id = id; }
    public void setExternal_id(String external_id) { this.externalId = external_id; }
    public void setStart_id(Long id) { this.startId = id; }
    public void setEnd_id(Long id) { this.endId = id; }
    public void setLink_type(LinkType type) { this.linkType = type; }
    public void setGeom(LineString geom) { this.geom = geom; }

}
