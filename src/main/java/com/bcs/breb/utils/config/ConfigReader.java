package com.bcs.breb.utils.config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("application.properties")) {
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar application.properties", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
