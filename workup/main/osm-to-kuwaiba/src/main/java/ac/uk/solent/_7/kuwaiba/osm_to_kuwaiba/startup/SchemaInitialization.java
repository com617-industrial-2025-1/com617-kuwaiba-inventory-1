package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.startup;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

@Service
public class SchemaInitialization {

    private static final Logger logger = LoggerFactory.getLogger(SchemaInitialization.class);

    @Autowired
    private DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void createSchema() throws Exception {
    	
        logger.info("Creating database schema from createtables.sql...");
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("createtables.sql"));
        }
        logger.info("Schema creation complete.");
    }	
}