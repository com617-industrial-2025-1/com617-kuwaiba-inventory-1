package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models.NetworkPoint;
import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models.PointType;

@SpringBootTest
@Transactional
public class NetworkPointTests {
	
	@Autowired
	private NetworkPointRepository networkPointRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@BeforeEach
	void insertTestPoles() {
		jdbcTemplate.execute("""
			INSERT INTO network_points (type, geom) VALUES
                -- Street 1: 8 poles spaced 50m apart on a road running east-west
                ('POLE', ST_GeomFromText('POINT(442000 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(442050 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(442100 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(442150 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(442200 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(442250 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(442300 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(442350 5425005)', 3857)),
                -- Street 2: same layout, 1000m east
                ('POLE', ST_GeomFromText('POINT(443000 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(443050 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(443100 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(443150 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(443200 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(443250 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(443300 5425005)', 3857)),
                ('POLE', ST_GeomFromText('POINT(443350 5425005)', 3857))
		"""); // generated test data but checked for relevance
	}
	
	// ------------------------
	// findByType() tests
	// -----------------------
	// HAS NOT BEEN TESTED WITH DOCKER RUNNING YET
	@Test
	void findByType_returnPolesWhenQueryingPole() {
		List<NetworkPoint> pointList = networkPointRepository.findByType(PointType.POLE);
		
		assertEquals(16, pointList.size());
		assertTrue(pointList.stream().allMatch(p -> p.getType() == PointType.POLE));
	}
	
	@Test
	void findByType_returnsEmptyListWhenTypeDoesNotExist() { // test data must not include cabinets for test
		List<NetworkPoint> pointList = networkPointRepository.findByType(PointType.CABINET);
		
		assertEquals(0, pointList.size());
		assertTrue(pointList.isEmpty());
	}
	
	// ------------------------------
	// findByParentId() tests
	// ------------------------------
	
	
	
	// ------------------------------
	// findByTypeAndParentId() tests
	// -------------------------------
	
	
	// ------------------------------
	// insertCabinetClusters() tests
	// ------------------------------
	
	
	// ----------------------------------
	// insertAggregatorClusters() tests
	// ----------------------------------
	
	
	// ----------------------------------
	// insertExchangeClusters() tests
	// ----------------------------------
	
	
	// ------------------------------------
	// updatePoleParents() tests
	// ------------------------------------
	
	
	// ----------------------------------
	// updateCabinetParents() tests
	// -----------------------------------
	
	// --------------------------------
	// updateAggregatorParents() tests
	// --------------------------------
}
