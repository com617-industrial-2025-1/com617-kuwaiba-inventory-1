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
	
	@Test
	void findByParentId_returnPolesWithMatchingParentId() {
		jdbcTemplate.execute("""
				UPDATE network_points SET parent_id = 999
				WHERE type = 'POLE' AND ST_X(geom) < 442400
		"""); // Sets 8 poles to the parent 999
		
		List<NetworkPoint> children = networkPointRepository.findByParentId(999L);
		
		assertEquals(8, children.size());
		assertTrue(children.stream().allMatch(p -> p.getParentId() == 999L));
	}
	
	@Test
	void findByParentId_returnsEmptyListWhenNoMatchingId() {
		List<NetworkPoint> children = networkPointRepository.findByParentId(99999L);
		
		assertTrue(children.isEmpty());
	}
	
	// ------------------------------
	// findByTypeAndParentId() tests
	// -------------------------------
	
	@Test
	void findByTypeAndParentId_returnsOnlyMatchingTypeAndParentId() {
		jdbcTemplate.execute("""
			UPDATE network_points
			SET parent_id = 777
			WHERE type = 'POLE'
		"""); // self explanatory
		
		List<NetworkPoint> children = networkPointRepository.findByTypeAndParentId(PointType.POLE, 777L);
		
		assertEquals(16, children.size());
		assertTrue(children.stream().allMatch(p -> p.getType() == PointType.POLE && p.getParentId() == 777L));
	}
	
	// ------------------------------
	// insertCabinetClusters() tests
	// ------------------------------
	
	@Test
	void insertCabinetClusters_createsCabinetForEachCluster() {
		networkPointRepository.insertCabinetClusters();
		
		int cabinetCount = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM network_points WHERE type = 'CABINET'",
			Integer.class
		); // Should return 2 as 16 poles / 8 = 2
		
		assertEquals(2, cabinetCount);
	}
	
	@Test
	void insertCabinetClusters_doesNotAffectExistingPoles() {
		networkPointRepository.insertCabinetClusters();
		
		int poleCount = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM network_points WHERE type = 'POLE'",
			Integer.class
		); // Should remain 16
		
		assertEquals(16, poleCount);
	}
	
	// ----------------------------------
	// insertAggregatorClusters() tests
	// ----------------------------------
	
	@Test
	void insertAggregatorClusters_createAggregatorForEachCluster() {
		networkPointRepository.insertCabinetClusters();
		networkPointRepository.insertAggregatorClusters();
		
		int aggregatorCount = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM network_points WHERE type = 'AGGREGATOR'",
			Integer.class
		);
		
		assertEquals(1, aggregatorCount);
	}
	
	// ----------------------------------
	// insertExchangeClusters() tests
	// ----------------------------------
	
	@Test
	void insertExchangeClusters_createExchangeForEachCluster() {
		networkPointRepository.insertCabinetClusters();
		networkPointRepository.insertAggregatorClusters();
		networkPointRepository.insertExchangeClusters();
		
		int exchangeCount = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM network_points WHERE type = 'EXCHANGE'",
				Integer.class
			);
			
		assertEquals(1, exchangeCount);
	}
	
	// ------------------------------------
	// updatePoleParents() tests
	// ------------------------------------
	
	@Test
	void updatePoleParents_setsParentIdOnEveryPole() {
		networkPointRepository.insertCabinetClusters();
		networkPointRepository.updatePoleParents();
		
		int unlinkedPoles = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM network_points WHERE type = 'POLE' AND parent_id IS NULL",
				Integer.class
		);
		
		assertEquals(0, unlinkedPoles);
	}
	
	@Test
	void updatePoleParents_linksToExistingCabinet() {
		networkPointRepository.insertCabinetClusters();
		networkPointRepository.updatePoleParents();
		
		int invalidParentCount = jdbcTemplate.queryForObject("""
			SELECT COUNT(*) FROM network_points poles
			WHERE poles.type = 'POLE'
			AND NOT EXISTS (
				SELECT 1 FROM network_points cabinets
				WHERE cabinets.id = poles.parent_id
				AND cabinets.type = 'CABINET'
			)
		""",
		Integer.class
		);
		
		assertEquals(0, invalidParentCount);
	}
	
	// ----------------------------------
	// updateCabinetParents() tests
	// -----------------------------------
	
	@Test
	void updateCabinetParents_setsParentIdOnEveryCabinet() {
        networkPointRepository.insertCabinetClusters();
        networkPointRepository.insertAggregatorClusters();
        networkPointRepository.updateCabinetParents();
        
        int unlinkedCabinets = jdbcTemplate.queryForObject(
        	"SELECT COUNT(*) FROM network_points WHERE type = 'CABINET' AND parent_id IS NULL",
        	Integer.class
        );
        
        assertEquals(0, unlinkedCabinets);
	}
	
	@Test
	void updateCabinetParents_linksToExistingAggregator() {
        networkPointRepository.insertCabinetClusters();
        networkPointRepository.insertAggregatorClusters();
        networkPointRepository.updateCabinetParents();
        
        int invalidParentCount = jdbcTemplate.queryForObject("""
    			SELECT COUNT(*) FROM network_points cabinets
    			WHERE cabinets.type = 'CABINET'
    			AND NOT EXISTS (
    				SELECT 1 FROM network_points aggregators
    				WHERE aggregators.id = cabinets.parent_id
    				AND aggregators.type = 'AGGREGATOR'
    			)
    		""",
    		Integer.class
        );
        
        assertEquals(0, invalidParentCount);
	}
	
	// --------------------------------
	// updateAggregatorParents() tests
	// --------------------------------
	
	@Test
	void updateAggregatorParents_setsParentIdOnEveryAggregator() {
		networkPointRepository.insertCabinetClusters();
        networkPointRepository.insertAggregatorClusters();
        networkPointRepository.insertExchangeClusters();
        networkPointRepository.updateAggregatorParents();
        
        int unlinkedAggregators = jdbcTemplate.queryForObject(
        	"SELECT COUNT(*) FROM network_points WHERE type = 'AGGREGATOR' AND parent_id IS NULL",
        	Integer.class
        );
        
        assertEquals(0, unlinkedAggregators);
	}
}
