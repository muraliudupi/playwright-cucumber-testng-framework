package com.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static volatile Properties properties;

    public static void loadProperties() {
        synchronized (ConfigReader.class) {
            if (properties != null) {
                return;
            }
            try (InputStream in = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("config/config.properties")) {
                if (in == null) {
                    throw new RuntimeException(
                            "config/config.properties not found on classpath. " +
                                    "Check it's under src/test/resources/config/.");
                }
                Properties loaded = new Properties();
                loaded.load(in);
                properties = loaded;
            } catch (IOException e) {
                throw new RuntimeException("Critical Failure: Could not load config.properties file configuration context", e);
            }
        }
    }

    public static String getProperty(String key) {
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(key);
    }

    public static String getExcelPath() {
        return get("excel.path");
    }

    public static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override;
        }
        return getProperty(key);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return (value != null && !value.isBlank()) ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException(String.format(
                    "Config Error: Property '%s' expected an integer but resolved to '%s'.", key, value));
        }
    }
}
