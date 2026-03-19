package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.locationtech.jts.geom.LineString;

@Entity
@Table(name = "cleaned_roads")
public class CleanedRoad {
    @Id
    @Column(name = "osm_id")
    private Long osmId;

    // TODO: find out road_type data type (String/Boolean)
    // private String road_type;
    @Column(name = "street_name")
    private String streetName;

    @Column(columnDefinition = "geometry(LineString, 3857)")
    private LineString geom;

}
