package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.LinkedBuilding;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.LinkedBuildingRepository;

@SpringBootTest
@Transactional
public class LinkedBuildingRepositoryTests {
	
	@Autowired 
	private LinkedBuildingRepository linkedBuildingRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@BeforeEach
    void setup() {
        jdbcTemplate.execute("DELETE FROM linked_buildings");
        jdbcTemplate.execute("""
            INSERT INTO linked_buildings (osm_id, building_name, house_num, street_name, floors, uprn, lat, lon)
            VALUES
                (1, 'Building A', '1', 'Test Street', '2', 100000001, 51.5074, -0.1278),
                (2, 'Building B', '2', 'Test Street', '1', 100000002, 51.5075, -0.1279)
        """);
    }

    // ----------------------------
    // findAll() tests
    // ----------------------------

    @Test
    void findAll_returnsAllLinkedBuildings() {
        assertEquals(2, linkedBuildingRepository.findAll().size());
    }

    // ----------------------------
    // findByUprn() tests
    // ----------------------------

    @Test
    void findByUprn_returnsCorrectBuilding() {
        List<LinkedBuilding> results = linkedBuildingRepository.findByUprn(100000001L);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getOsmId());
    }

    @Test
    void findByUprn_returnsEmptyListWhenUprnDoesNotExist() {
        List<LinkedBuilding> results = linkedBuildingRepository.findByUprn(999999999L);
        assertTrue(results.isEmpty());
    }

    // ----------------------------
    // createLinkedBuildings() tests
    // ----------------------------

    // Cannot easily replicate the data as it comes in many tables.

    @Test
    void createLinkedBuildings_doesNotThrow() {
        jdbcTemplate.execute("DELETE FROM linked_buildings");
        assertDoesNotThrow(() -> linkedBuildingRepository.createLinkedBuildings());
    }

    @Test
    void createLinkedBuildings_tableIsQueryableAfterCall() {
        jdbcTemplate.execute("DELETE FROM linked_buildings");
        linkedBuildingRepository.createLinkedBuildings();
        int count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM linked_buildings",
            Integer.class
        );
        assertNotNull(count);
    }
}