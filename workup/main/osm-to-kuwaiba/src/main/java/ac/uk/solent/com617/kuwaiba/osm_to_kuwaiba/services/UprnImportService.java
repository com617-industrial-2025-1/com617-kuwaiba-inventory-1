package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services;

import java.io.FileReader;
import java.sql.Connection;

import javax.sql.DataSource;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UprnImportService {
	// logger tied to this class that logs output in console
	private static final Logger logger = LoggerFactory.getLogger(UprnImportService.class);
	
	@Value("${uprn.csv.path}")
	private String uprnCsvPath;
	
	@Autowired
	private DataSource dataSource; // gets database credentials from application.properties
	
	public void importUprns() throws Exception {
        logger.info("Importing UPRN CSV into raw_uprns...");

        try (Connection conn = dataSource.getConnection()) { // borrows connection from spring boot connection pool.
            BaseConnection pgConn = conn.unwrap(BaseConnection.class); // unwrapping the spring postgresql connection.
            CopyManager copyManager = new CopyManager(pgConn);

            try (FileReader reader = new FileReader(uprnCsvPath)) { // uprnCsvPath might need to be changed when spring boot
            														// application gets containerised.
            														// How it can be overriden in docker compose
            														// environment:
            	  													//	- UPRN_CSV_PATH=/data/uprns.csv
                copyManager.copyIn("COPY raw_uprns FROM STDIN WITH CSV HEADER", reader);
            }
        }

        logger.info("UPRN import complete.");
    }
	
}