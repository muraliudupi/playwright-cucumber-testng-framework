package com.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads src/test/resources/config/config.properties once at classload time, then layers System properties (-DbaseUrl=..., set from build.gradle) on
 * top so CI can override any value without touching the properties file.
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (in != null) {
                PROPERTIES.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        // System property (set via -D or Gradle systemProperty) always wins
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override;
        }
        return PROPERTIES.getProperty(key);
    }
}
