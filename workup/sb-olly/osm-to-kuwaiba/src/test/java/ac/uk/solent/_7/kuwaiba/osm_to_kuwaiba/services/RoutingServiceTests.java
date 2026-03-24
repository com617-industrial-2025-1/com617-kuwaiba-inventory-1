package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class RoutingServiceTests {
	
    @Autowired
    private RoutingService routingService;
 
    @Autowired
    private JdbcTemplate jdbcTemplate;
 
    @BeforeEach
    void insertTestHierarchy() {
        jdbcTemplate.execute("""
            INSERT INTO network_points (id, type, geom) VALUES
                (1, 'POLE',       ST_GeomFromText('POINT(442066 5425005)', 3857)),
                (2, 'CABINET',    ST_GeomFromText('POINT(442200 5425005)', 3857)),
                (3, 'AGGREGATOR', ST_GeomFromText('POINT(442500 5425005)', 3857)),
                (4, 'EXCHANGE',   ST_GeomFromText('POINT(443000 5425005)', 3857))
        """);
 
        jdbcTemplate.execute("UPDATE network_points SET parent_id = 2 WHERE id = 1");
        jdbcTemplate.execute("UPDATE network_points SET parent_id = 3 WHERE id = 2");
        jdbcTemplate.execute("UPDATE network_points SET parent_id = 4 WHERE id = 3");
 
        jdbcTemplate.execute("""
            INSERT INTO building_drop_points (building_id, parent_id, geom) VALUES
                (1, 1, ST_GeomFromText('POINT(442032 5425000)', 3857)),
                (2, 1, ST_GeomFromText('POINT(442044 5425000)', 3857))
        """);
    } // generated test data
    
    // ---------------------------
    // runFullPrediction()
    // ---------------------------
    
    @Test
    void runFullPrediction_returnsSuccessMessage() {
    	String result = routingService.runFullPrediction();
        assertEquals("Routing Prediction Completed.", result);
    }
    
    @Test
    void runFullPrediction_createsDropConnections() {
        routingService.runFullPrediction();
 
        int dropCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'DROP'",
            Integer.class
        );
        assertEquals(2, dropCount);
    }
    
    @Test
    void runFullPrediction_createsFeederConnections() {
        routingService.runFullPrediction();
 
        int feederCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'FEEDER'",
            Integer.class
        );
        assertEquals(1, feederCount);
    }
    
    @Test
    void runFullPrediction_createsDistributionConnections() {
        routingService.runFullPrediction();
 
        int distributionCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'DISTRIBUTION'",
            Integer.class
        );
        assertEquals(1, distributionCount);
    }
    
    @Test
    void runFullPrediction_createsTrunkConnections() {
        routingService.runFullPrediction();
 
        int trunkCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'TRUK'",
            Integer.class
        );
        assertEquals(1, trunkCount);
    }
    
    // -----------------------
    // Testing individual level predictions
    // -------------------------
    
    @Test
    void poleToBuildingPrediction_createsDropConnections() {
        routingService.poleToBuildingPrediction();
 
        int dropCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'DROP'",
            Integer.class
        );
        assertEquals(2, dropCount);
    }
 
    @Test
    void cabinetToPolePrediction_createsFeederConnections() {
        routingService.cabinetToPolePrediction();
 
        int feederCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'FEEDER'",
            Integer.class
        );
        assertEquals(1, feederCount);
    }
 
    @Test
    void aggregatorToCabinetPrediction_createsDistributionConnections() {
        routingService.aggregatorToCabinetPrediction();
 
        int distributionCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'DISTRIBUTION'",
            Integer.class
        );
        assertEquals(1, distributionCount);
    }
 
    @Test
    void exchangeToAggregatorPrediction_createsTrunkConnections() {
        routingService.exchangeToAggregatorPrediction();
 
        int trunkCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM network_connections WHERE link_type = 'TRUK'",
            Integer.class
        );
        assertEquals(1, trunkCount);
}
