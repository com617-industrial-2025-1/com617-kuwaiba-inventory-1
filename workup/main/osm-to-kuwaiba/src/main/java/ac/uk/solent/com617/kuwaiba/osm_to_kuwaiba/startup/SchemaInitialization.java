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
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

@Service
public class SchemaInitialization {

    private static final Logger logger = LoggerFactory.getLogger(SchemaInitialization.class);

    @Autowired
    private DataSource dataSource;
    
    
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
 
}