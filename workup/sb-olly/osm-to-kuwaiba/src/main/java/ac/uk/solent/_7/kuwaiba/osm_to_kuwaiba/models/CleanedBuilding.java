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
    @Column(name = "osm_id")
    private Long osmId;

    @Column(columnDefinition = "geometry(MultiPolygon, 3857)")
    private MultiPolygon geom;

    @Column(name = "building_name")
    private String buildingName;
    
    @Column(name = "house_num")
    private String houseNum;
    
    @Column(name = "street_name")
    private String streetName;
    
    private String floors;

}
