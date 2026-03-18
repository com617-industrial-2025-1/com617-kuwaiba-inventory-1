package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table(name = "cleaned_buildings")
public class CleanedBuilding {
    @Id
    private Long osm_id;

    @Column(columnDefinition = "geometry(MultiPolygon, 3857)")
    private MultiPolygon geom;

    private String building_name;
    private String house_num;
    private String street_name;
    private String floors;

}
