package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services.ClusteringService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ClusteringServiceTests {
	
	@Autowired
	private ClusteringService clusteringService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@BeforeEach
	void insertTestData() {
		// Deleting data from tables to not polute tests.
		jdbcTemplate.execute("DELETE FROM building_drop_points");
		jdbcTemplate.execute("DELETE FROM network_points");
		jdbcTemplate.execute("DELETE FROM network_connections");
		
        jdbcTemplate.execute("""
                INSERT INTO building_drop_points (building_id, geom) VALUES
                    -- Street 1: 12 houses spaced 12m apart
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
                    -- Street 2: same layout, 1000m east
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
            """); // generated test data but verified as useful
	}

	// ---------------------------
	// runFullPrediction()
	// ---------------------------
	
	@Test
	void runFullPrediction_returnsSuccessMessage() {
		String result = clusteringService.runFullPrediction();
		assertEquals("Clustering Prediction Completed.", result);
	}
	
	@Test
	void runFullPrediction_deletesExistingData() {
	    jdbcTemplate.execute("""
	        INSERT INTO network_points (type, geom)
	        VALUES ('POLE', ST_GeomFromText('POINT(999999 999999)', 3857))
	    """);
	 
	    clusteringService.runFullPrediction();
	 
	    int stalePoints = jdbcTemplate.queryForObject(
	        "SELECT COUNT(*) FROM network_points WHERE ST_X(geom) = 999999",
	        Integer.class
	    );
	    assertEquals(0, stalePoints);
	        
	}
	
	@Test
	void runFullPrediction_createsTwoPoles() {
		clusteringService.runFullPrediction();
		
		int poleCount = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM network_points WHERE type = 'POLE'",
			Integer.class
		);
		assertEquals(2, poleCount);
	}
	
	@Test
	void runFullPrediction_createsOneCabinet() {
		clusteringService.runFullPrediction();
		
		int cabinetCount = jdbcTemplate.queryForObject(
	        "SELECT COUNT(*) FROM network_points WHERE type = 'CABINET'",
	        Integer.class
	    );
		assertEquals(1, cabinetCount);
	}
	
	@Test
	void runFullPrediction_createsOneAggregator() {
		clusteringService.runFullPrediction();
		 
        int aggregatorCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_points WHERE type = 'AGGREGATOR'",
            Integer.class
        );
        assertEquals(1, aggregatorCount);
	}
	
	@Test
	void runFullPrediction_createsOneExchange() {
        clusteringService.runFullPrediction();
        
        int exchangeCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_points WHERE type = 'EXCHANGE'",
            Integer.class
        );
        assertEquals(1, exchangeCount);
	}
	
    @Test
    void runFullPrediction_allBuildingsLinkedToPole() {
        clusteringService.runFullPrediction();
 
        int unlinkedBuildings = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM building_drop_points WHERE parent_id IS NULL",
            Integer.class
        );
        assertEquals(0, unlinkedBuildings);
    }
 
    @Test
    void runFullPrediction_allPolesLinkedToCabinet() {
        clusteringService.runFullPrediction();
 
        int unlinkedPoles = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_points WHERE type = 'POLE' AND parent_id IS NULL",
            Integer.class
        );
        assertEquals(0, unlinkedPoles);
    }
    
    // TODO: Add in test for checking cabinets are linked to aggregators and aggregators to exchanges
    // TODO: 
	
}
