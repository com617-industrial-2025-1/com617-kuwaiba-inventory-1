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
            System.out.println("Connecting to PostGIS...");

            Statement stmt = conn.createStatement();

            stmt.execute("DROP TABLE IF EXISTS raw_uprns;");
            stmt.execute("CREATE TABLE raw_uprns (" +
                    "uprn BIGINT, " +
                    "lat DOUBLE PRECISION, " +
                    "lon DOUBLE PRECISION);");

            System.out.println("Uploading UPRN CSV...");

            BaseConnection pgConn = conn.unwrap(BaseConnection.class);
            CopyManager copyManager = new CopyManager(pgConn);

            FileReader reader = new FileReader("/data/uprns.csv");
            copyManager.copyIn("COPY raw_uprns FROM STDIN WITH CSV HEADER", reader);

            System.out.println("Linking UPRNs to building footprints...");

            stmt.execute("DROP TABLE IF EXISTS final_linked_network;");
            stmt.execute(
                    "CREATE TABLE final_linked_network AS " +
                            "SELECT b.*, u.uprn " +
                            "FROM cleaned_buildings b " +
                            "JOIN raw_uprns u ON ST_Contains(b.geom, ST_Transform(ST_SetSRID(ST_Point(u.lon, u.lat), 4326), 3857));");

            System.out.println("SUCCESS: UPRN mapping complete. Check table 'final_linked_network'.");

        } catch (Exception e) {
            System.err.println("Error during linking: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}