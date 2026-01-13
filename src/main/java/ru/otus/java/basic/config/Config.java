package ru.otus.java.basic.config;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = Config.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (in == null) {
                throw new RuntimeException("application.properties not found");
            }
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }

    public static int serverPort() {
        return Integer.parseInt(props.getProperty("server.port", "8189"));
    }

    public static int threadPoolSize() {
        return Integer.parseInt(props.getProperty("server.poolSize", "4"));
    }

    public static int maxRequestSize() {
        return Integer.parseInt(props.getProperty("http.maxRequestSize", "1048576"));
    }

    public static int maxResponseSize() {
        return Integer.parseInt(props.getProperty("http.maxResponseSize", "5242880"));
    }
}
