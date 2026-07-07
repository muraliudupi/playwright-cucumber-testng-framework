package com.framework.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DatabaseUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseUtil.class);
    private static HikariDataSource dataSource = null;

    private static synchronized HikariDataSource getDataSource() {
        if (dataSource == null) {
            String dbUrl = ConfigReader.get("db.url");
            if (dbUrl == null || dbUrl.contains("your-enterprise-db")) {
                throw new IllegalStateException("Database connection pool accessed but 'db.url' is unconfigured or a placeholder.");
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            config.setUsername(ConfigReader.get("db.user"));

            String runtimePassword = System.getProperty("db.password", System.getenv("DB_PASSWORD"));
            config.setPassword((runtimePassword == null || runtimePassword.isBlank()) ? "" : runtimePassword);

            config.setMaximumPoolSize(15);
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(20000);
            config.setPoolName("Playwright-Automation-DBPool");

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    private DatabaseUtil() {}

    public static String getSingleValue(String query, String columnName, Object... params) {
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String value = rs.getString(columnName);
                    if (value == null) {
                        throw new RuntimeException(String.format(
                                "Database integrity anomaly: Column '%s' exists but resolved to a NULL value for query [%s]", columnName, query));
                    }
                    return value;
                } else {
                    throw new RuntimeException(String.format(
                            "Database verification error: Zero records returned from the ledger data pipeline for query: [%s]", query));
                }
            }
        } catch (SQLException e) {
            LOG.error("Database query failed under concurrent execution pool context: {}", e.getMessage());
            throw new RuntimeException("Database automation cross-check pipeline crash.", e);
        }
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOG.info("Database connection pool closed successfully.");
        }
    }
}