package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository;

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
	
	
}
