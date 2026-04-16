package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services.UprnService;

@SpringBootTest
@Transactional
public class UprnServiceTests {
	
	@Autowired
	private UprnService uprnService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@BeforeEach
	void setup() {
		jdbcTemplate.execute("DELETE FROM linked_buildings");
        jdbcTemplate.execute("DELETE FROM raw_uprns");
	}
	
	@Test
	void linksUprns_returnsSuccessMessage() {
		String result = uprnService.linksUprns();
		assertEquals("UPRN linking complete.", result);
	}
	
	@Test
	void linksUprns_tableExistsAfterCall() {
		uprnService.linksUprns();
		int count = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM linked_buildings",
			Integer.class
		);
		assertNotNull(count);
	}
	
	@Test
	void linked_buildings_tableExists() {
	    int count = jdbcTemplate.queryForObject(
	        "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'linked_buildings'",
	        Integer.class
	    );
	    assertEquals(1, count);
	}
}