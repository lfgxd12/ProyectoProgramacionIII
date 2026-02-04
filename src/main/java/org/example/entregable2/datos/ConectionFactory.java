package org.example.entregable2.datos;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.StringJoiner;

public class ConectionFactory {

    private static final String PROPS_FILE = "db.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream in = ConectionFactory.class.getClassLoader().getResourceAsStream(PROPS_FILE)) {
            if (in == null) {
                throw new RuntimeException("No se encontró " + PROPS_FILE + " en resources.");
            }

            Properties p = new Properties();
            p.load(in);

            url = p.getProperty("db.url");
            user = p.getProperty("db.user");
            password = p.getProperty("db.password");

            if (url == null || user == null) {
                throw new RuntimeException("Faltan propiedades db.url o db.user en " + PROPS_FILE);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error cargando configuración del sistema: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar a la base de datos: " + e.getMessage(), e);
        }
    }
}