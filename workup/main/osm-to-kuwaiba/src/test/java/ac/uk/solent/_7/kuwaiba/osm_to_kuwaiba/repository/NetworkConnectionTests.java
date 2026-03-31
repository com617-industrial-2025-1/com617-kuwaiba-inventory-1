package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class NetworkConnectionTests {
	
	@Autowired
	private NetworkConnectionRepository connectionRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@BeforeEach
	void insertTestData() {
		// Deleting data from tables to not pollute tests.
		jdbcTemplate.execute("DELETE FROM network_connections");
		jdbcTemplate.execute("DELETE FROM network_points");
		jdbcTemplate.execute("DELETE FROM building_drop_points");
		
		// Network points on a road running east-west at Y=5425005 (road centre)
        // Pole is 5m north of the houses (Y=5425000 → Y=5425005)
        jdbcTemplate.execute("""
            INSERT INTO network_points (id, type, geom) VALUES
                (1, 'POLE',       ST_GeomFromText('POINT(442066 5425005)', 3857)),
                (2, 'CABINET',    ST_GeomFromText('POINT(442200 5425005)', 3857)),
                (3, 'AGGREGATOR', ST_GeomFromText('POINT(442500 5425005)', 3857)),
                (4, 'EXCHANGE',   ST_GeomFromText('POINT(443000 5425005)', 3857))
        """);
 
        // Link hierarchy: pole → cabinet → aggregator → exchange
        jdbcTemplate.execute("UPDATE network_points SET parent_id = 2 WHERE id = 1");
        jdbcTemplate.execute("UPDATE network_points SET parent_id = 3 WHERE id = 2");
        jdbcTemplate.execute("UPDATE network_points SET parent_id = 4 WHERE id = 3");
 
        // Two houses on the south side of the road, linked to the pole
        // Houses are 12m apart, ~66m from the pole — within the 68m requirement
        jdbcTemplate.execute("""
            INSERT INTO building_drop_points (building_id, parent_id, geom) VALUES
                (1, 1, ST_GeomFromText('POINT(442032 5425000)', 3857)),
                (2, 1, ST_GeomFromText('POINT(442044 5425000)', 3857))
        """); // Prompted for test data but checked for relevance
	}
	
	// --------------------
	// insertDropConnections()
	// ---------------------
	
	@Test
	void insertDropConnections_createConnectionForEachBuilding() {
		connectionRepository.insertDropConnections();
		
		int dropCount = jdbcTemplate.queryForObject(
	            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'DROP'"
				,Integer.class
		);
	    assertEquals(2, dropCount);
	}
	
	@Test
	void insertDropConnections_linksBuildingToParentPole() {
		connectionRepository.insertDropConnections();
		
		int invalidCount = jdbcTemplate.queryForObject("""
	            SELECT COUNT(*) FROM network_connections nc
	            WHERE nc.link_type = 'DROP'
	            AND NOT EXISTS (
	                SELECT 1 FROM network_points np
	                WHERE np.id = nc.start_id AND np.type = 'POLE'
	            )
	            """,
	            Integer.class
		);
	    assertEquals(0, invalidCount);
	}
	
	// -----------------------
	// insertFeederConnections()
	// -------------------------
	
	@Test
	void insertFeederConnections_createConnectionForEachPole() {
        connectionRepository.insertFeederConnections();
        
        int feederCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'FEEDER'",
            Integer.class
        );
        assertEquals(1, feederCount);
	}
	
	@Test
	void insertFeederConnections_linksPoletoParentCabinet() {
		connectionRepository.insertFeederConnections();
        
        int invalidCount = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM network_connections nc
            WHERE nc.link_type = 'FEEDER'
            AND NOT EXISTS (
                SELECT 1 FROM network_points np
                WHERE np.id = nc.start_id AND np.type = 'CABINET'
            )
            """,
            Integer.class
        );
        assertEquals(0, invalidCount);
	}
	
	// ---------------------------
	// insertDistributionConnections()
	// ----------------------------
	
	@Test
	void insertDistributionConnections_createConnectionForEachCabinet() {
        connectionRepository.insertDistributionConnections();
        
        int distributionCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'DISTRIBUTION'",
            Integer.class
        );
        assertEquals(1, distributionCount);
	}
	
	@Test
	void insertDistributionConnections_linksCabinetToAggregator() {
		connectionRepository.insertDistributionConnections();
		 
        int invalidCount = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM network_connections nc
            WHERE nc.link_type = 'DISTRIBUTION'
            AND NOT EXISTS (
                SELECT 1 FROM network_points np
                WHERE np.id = nc.start_id AND np.type = 'AGGREGATOR'
            )
            """,
            Integer.class
        );
        assertEquals(0, invalidCount);
	}
	
	// ----------------------------
	// insertTrunkConnections()
	// ----------------------------
	
	@Test
	void insertTrunkConnections_createConnectionForEachAggregate() {
		connectionRepository.insertTrunkConnections();
		 
        int trunkCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'TRUNK'",
            Integer.class
        );
        assertEquals(1, trunkCount);
	}
	
	@Test
	void insertTrunkConnections_linksAggregatorToExchange() {
        connectionRepository.insertTrunkConnections();
        
        int invalidCount = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM network_connections nc
            WHERE nc.link_type = 'TRUNK'
            AND NOT EXISTS (
                SELECT 1 FROM network_points np
                WHERE np.id = nc.start_id AND np.type = 'EXCHANGE'
            )
            """,
            Integer.class
        );
        assertEquals(0, invalidCount);
	}
	
}
