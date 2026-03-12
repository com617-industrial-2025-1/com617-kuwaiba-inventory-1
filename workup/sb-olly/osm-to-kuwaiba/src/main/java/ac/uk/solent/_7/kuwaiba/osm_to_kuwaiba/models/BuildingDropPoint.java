package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.locationtech.jts.geom.Point;

@Entity
@Immutable // can be queried without being changed
@Table(name = "building_drop_points")
public class BuildingDropPoint {
    @Id
    private Long id;

    @Column(columnDefinition = "geometry(Point, 3857)")
    private Point geom;

}
