package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class BuildingDropPointTests {
	
	@Autowired
	private BuildingDropPointRepository dropPointRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@BeforeEach
	void insertTestData() {
		jdbcTemplate.execute("""
			INSERT INTO building_drop_points (building_id, geom) VALUES
	            (1,  ST_GeomFromText('POINT(442000 5425000)', 3857)),
	            (2,  ST_GeomFromText('POINT(442012 5425000)', 3857)),
	            (3,  ST_GeomFromText('POINT(442024 5425000)', 3857)),
	            (4,  ST_GeomFromText('POINT(442036 5425000)', 3857)),
	            (5,  ST_GeomFromText('POINT(442048 5425000)', 3857)),
	            (6,  ST_GeomFromText('POINT(442060 5425000)', 3857)),
	            (7,  ST_GeomFromText('POINT(442072 5425000)', 3857)),
	            (8,  ST_GeomFromText('POINT(442084 5425000)', 3857)),
	            (9,  ST_GeomFromText('POINT(442096 5425000)', 3857)),
	            (10, ST_GeomFromText('POINT(442108 5425000)', 3857)),
	            (11, ST_GeomFromText('POINT(442120 5425000)', 3857)),
	            (12, ST_GeomFromText('POINT(442132 5425000)', 3857)),
	            (13, ST_GeomFromText('POINT(443000 5425000)', 3857)),
	            (14, ST_GeomFromText('POINT(443012 5425000)', 3857)),
	            (15, ST_GeomFromText('POINT(443024 5425000)', 3857)),
	            (16, ST_GeomFromText('POINT(443036 5425000)', 3857)),
	            (17, ST_GeomFromText('POINT(443048 5425000)', 3857)),
	            (18, ST_GeomFromText('POINT(443060 5425000)', 3857)),
	            (19, ST_GeomFromText('POINT(443072 5425000)', 3857)),
	            (20, ST_GeomFromText('POINT(443084 5425000)', 3857)),
	            (21, ST_GeomFromText('POINT(443096 5425000)', 3857)),
	            (22, ST_GeomFromText('POINT(443108 5425000)', 3857)),
	            (23, ST_GeomFromText('POINT(443120 5425000)', 3857)),
	            (24, ST_GeomFromText('POINT(443132 5425000)', 3857))
		""");
	}
	
	// --------------------------------------
	// insertPoleClusters() tests
	// --------------------------------------
	
	
	@Test
	void insertPoleClusters_createsPoleForEachCluster() {
		dropPointRepository.insertPoleClusters();
		
		int poleCount = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM network_points WHERE type = 'POLE'",
			Integer.class
		);
		assertEquals(2, poleCount);
	}
	
	@Test
	void insertPoleClusters_onlyCreatesPoles() {
		dropPointRepository.insertPoleClusters();
		
		int nonPoleCount = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM network_points WHERE type != 'POLE'",
			Integer.class
		);
		assertEquals(0, nonPoleCount);
	}
	
	@Test
	void insertPoleClusters_doesNotCreatePolesWhenNoBuildingsExist() {
		jdbcTemplate.execute(
				"DELETE FROM building_drop_points" // removing the test data
		);
		
		// should insert no rows and not throw error
		assertDoesNotThrow(() -> dropPointRepository.insertPoleClusters());
		
		int poleCount = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM network_points WHERE type = 'POLE'",
			Integer.class
		);
		
		assertEquals(0, poleCount);
		
	}
	
	// -----------------------------------
	// updateBuildingParents() tests
	// -----------------------------------
	// have to insertPoleClusters() before updateBuildingParents() can work
	
	@Test
	void updateBuildingParents_setParentIdOnEveryBuilding() {
		dropPointRepository.insertPoleClusters();
		
		dropPointRepository.updateBuildingParents();
		
		int incompParentFieldCount = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM building_drop_points WHERE parent_id IS NULL",
			Integer.class
		);
		
		assertEquals(0, incompParentFieldCount);
	}
	
	@Test
	void updateBuildingParents_bdpParentIdLinksToPole() {
		dropPointRepository.insertPoleClusters();
		
		dropPointRepository.updateBuildingParents();
		
		int invalidParentCount = jdbcTemplate.queryForObject("""
			SELECT COUNT(*) FROM building_drop_points bdp
			WHERE NOT EXISTS(
				SELECT 1 FROM network_points np
				WHERE np.id = bdp.parent_id AND np.type = 'POLE'
			)	
			""",
			Integer.class
		);
		
		assertEquals(0, invalidParentCount);
		
	}
}
