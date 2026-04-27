package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.LineStringSerializer;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Geometry;

import jakarta.persistence.Embeddable;

@Embeddable
public class KeyValuePair {
    private String key;
    private String value;

    public KeyValuePair() {}

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}

@Entity
@Table(name = "network_connections")
public class NetworkConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "external_id", columnDefinition = "TEXT")
    private String externalId;
    
    @Column(name = "start_id")
    private Long startId;
    
    @Column(name = "end_id")
    private Long endId;

    @Column(name = "street_name", columnDefinition = "TEXT")
    private String streetName;

    @Column(name = "osm_id")
    private Long osmId;

    // Tells JPA to store the enum string in the database instead of number.
    @Enumerated(EnumType.STRING)
    @Column(name = "link_type")
    private LinkType linkType;

    // Using SRID 3857
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(columnDefinition = "geometry(LineString, 3857)")
    private Geometry geom;
    
    // to store key-value pairs for the point, we can use a Map<String, KeyValuePair> where 
    // KeyValuePair is a simple class with 'key' and 'value' fields.
    // see https://stackoverflow.com/questions/7131440/storing-key-value-pairs-in-hibernate-can-we-use-map
    @ElementCollection(fetch=FetchType.EAGER)
    @MapKeyColumn(name="key")
    @CollectionTable(name="keyvaluepair", joinColumns= @JoinColumn(name="id"))
    private Map<String, KeyValuePair> keyValuePairMap = 
            new HashMap<String, KeyValuePair>();

    // getters and setters
    public Long getId() { return id; }
    public String getExternal_id() { return externalId; }
    public Long getStart_id() { return startId; }
    public Long getEnd_id() { return endId; }
    public LinkType getLink_type() { return linkType; }
    public String getStreetName() { return streetName; }
    public Long getOsmId() { return osmId; }

    public Geometry getGeom() { return geom; }

    public void setId(Long id) { this.id = id; }
    public void setExternal_id(String external_id) { this.externalId = external_id; }
    public void setStart_id(Long id) { this.startId = id; }
    public void setEnd_id(Long id) { this.endId = id; }
    public void setLink_type(LinkType type) { this.linkType = type; }
    public void setGeom(Geometry geom) { this.geom = geom; }
    public void setStreetName(String streetName) { this.streetName = streetName; }
    public void setOsmId(Long osmId) { this.osmId = osmId; }
    
    public Map<String, KeyValuePair> getKeyValuePairMap() { return keyValuePairMap; }
    public void setKeyValuePairMap(Map<String, KeyValuePair> map) { this.keyValuePairMap = map; }

}
