package com.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseUtil.class);

    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

    static {
        DB_URL = ConfigReader.get("db.url");
        DB_USER = ConfigReader.get("db.user");

        // 2. Resolve password from JVM system arguments (-Ddb.password) or fallback to local system environment (DB_PASSWORD)
        String runtimePassword = System.getProperty("db.password", System.getenv("DB_PASSWORD"));

        if (runtimePassword == null || runtimePassword.isBlank()) {
            LOG.warn("CRITICAL ARCHITECTURE WARNING: Database verification password resolved to an empty or null reference.");
            DB_PASSWORD = "";
        } else {
            DB_PASSWORD = runtimePassword;
        }
    }

    public static String getSingleValue(String query, String columnName, Object... params) {
        if (DB_URL == null || DB_URL.isBlank()) {
            throw new RuntimeException("Database connection cannot proceed. 'db.url' property is undefined.");
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(columnName);
                }
            }
        } catch (SQLException e) {
            LOG.error("Database validation transaction crashed during runtime execution path: {}", e.getMessage());
            throw new RuntimeException("Database query failure on automated pipeline validation step.", e);
        }
        return null;
    }
}