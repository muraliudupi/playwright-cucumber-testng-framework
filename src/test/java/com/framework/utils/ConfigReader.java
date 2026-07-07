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
        return System.getProperty("user.dir") + "/" + get("excel.path");
    }

    public static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override;
        }
        return getProperty(key);
    }
}