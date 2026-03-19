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
    private Long osm_id;

    // TODO: find out road_type data type (String/Boolean)
    // private String road_type;
    private String street_name;

    @Column(columnDefinition = "geometry(LineString, 3857)")
    private LineString geom;

}
