package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "building_drop_points")
public class BuildingDropPoint {
    @Id
    @Column(name = "building_id")
    private Long buildingId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(columnDefinition = "geometry(Point, 3857)")
    private Point geom;

}
