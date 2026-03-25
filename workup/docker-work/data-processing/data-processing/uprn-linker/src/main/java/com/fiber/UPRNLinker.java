package com.fiber;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.postgresql.core.BaseConnection;
import org.postgresql.copy.CopyManager;

public class UPRNLinker {
    public static void main(String[] args) {
        // Gets database from the Docker environment variables
        String dbUrl = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass)) {

            System.out.println("Uploading UPRN CSV...");

            BaseConnection pgConn = conn.unwrap(BaseConnection.class);
            CopyManager copyManager = new CopyManager(pgConn);

            FileReader reader = new FileReader("/data/uprns.csv");
            copyManager.copyIn("COPY raw_uprns FROM STDIN WITH CSV HEADER", reader);

            System.out.println("SUCCESS: UPRN data loaded.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}