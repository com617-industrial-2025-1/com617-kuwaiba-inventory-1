package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.startup;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

@Service
public class SchemaInitialization {

    private static final Logger logger = LoggerFactory.getLogger(SchemaInitialization.class);

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /*
     * While createSchema doesn't create tables anymore (hibernate does), it creates the indexes 
     * that need to be used. So still needs to be ran.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void createSchema() throws Exception {
    	logger.info("Creating Tables from schema.sql...");
    	try (Connection conn = dataSource.getConnection()) {
    		ScriptUtils.executeSqlScript(conn, new ClassPathResource("schema.sql"));
    	}
    	logger.info("Table Indexing Complete.");
    }
    
    // Having to move all 
    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
    public void populateTables() throws Exception {
    	logger.info("Populating tables from createtables.sql...");
    	try (Connection conn = dataSource.getConnection()) {
    		ScriptUtils.executeSqlScript(conn, new ClassPathResource("createtables.sql"));
    	}
    	logger.info("Table Population Complete.");
    }
    
    // Building the topology for the routing of connections
    @EventListener(ApplicationReadyEvent.class)
    @Order(4)
    public void buildTopology() throws Exception {
        logger.info("Building pgRouting topology...");
        Integer vertexCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables " +
            "WHERE table_name = 'noded_streets_vertices_pgr'", Integer.class);
        
        if (vertexCount == null || vertexCount == 0) {
            logger.info("Creating topology...");
            jdbcTemplate.execute(
                "SELECT pgr_createTopology('noded_streets', 1.0, 'geom', 'id')"
            );
            
            logger.info("Calculating edge costs...");
            jdbcTemplate.execute(
                "UPDATE noded_streets SET cost = ST_Length(geom) WHERE cost IS NULL"
            );
            logger.info("Topology build complete.");
        } else {
            logger.info("Topology already exists, skipping.");
        }
    }
 
}