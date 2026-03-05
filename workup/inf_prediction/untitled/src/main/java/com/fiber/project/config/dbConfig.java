package com.fiber.project.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class dbConfig {
    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();

            // using environment variables
            String dbUrl = "jdbc:postgresql://localhost:5432/" + System.getenv("DB_NAME");
            config.setJdbcUrl(dbUrl);
            config.setUsername(System.getenv("DB_USER"));
            config.setPassword(System.getenv("DB_PASSWORD"));

            // optimizing for performance
            config.setMaximumPoolSize(10);
            config.setDriverClassName("org.postgresql.Driver");

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
}
