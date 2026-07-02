package com.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    public static void loadProperties() {
        try (FileInputStream fis = new FileInputStream("src/test/resources/config/config.properties")) {
            properties = new Properties();
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Critical Failure: Could not load config.properties file configuration context", e);
        }
    }

    public static String getProperty(String key) {
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(key);
    }

    // ARCHITECTURAL ACCESSOR: Resolves the fully qualified path dynamically across OS layers
    public static String getExcelPath() {
        return System.getProperty("user.dir") + "/" + getProperty("excel.path");
    }

    public static String get(String key) {
        return getProperty(key);
    }
}