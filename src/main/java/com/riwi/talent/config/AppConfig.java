package com.riwi.talent.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton que centraliza la configuración de la aplicación.
 * Carga database.properties y app.properties desde el classpath
 * (src/main/resources), igual que el proyecto de referencia.
 *
 * Ventaja frente a variables de entorno hardcodeadas (Java 8 legacy):
 * los archivos .properties son fáciles de cambiar sin recompilar,
 * se pueden excluir del control de versiones con .gitignore, y
 * permiten configuraciones distintas por entorno (dev / prod).
 */
public class AppConfig {

    private static AppConfig instance;

    private final Properties dbProps  = new Properties();
    private final Properties appProps = new Properties();

    private AppConfig() {
        load("database.properties", dbProps);
        load("app.properties",      appProps);
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) instance = new AppConfig();
            }
        }
        return instance;
    }

    // ── Carga segura con try-with-resources ──────────────────────────────
    private void load(String filename, Properties target) {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(filename)) {
            if (is == null) throw new RuntimeException(
                    "No se encontró en el classpath: " + filename);
            target.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Error cargando " + filename, e);
        }
    }

    // ── Getters de base de datos ─────────────────────────────────────────
    public String getDbUrl()      { return dbProps.getProperty("db.url"); }
    public String getDbUser()     { return dbProps.getProperty("db.user"); }
    public String getDbPassword() { return dbProps.getProperty("db.password"); }
    public String getDbDriver()   { return dbProps.getProperty("db.driver"); }

    // ── Getters de aplicación ────────────────────────────────────────────
    public String getAppName()  { return appProps.getProperty("app.name"); }
    public String getViewType() { return appProps.getProperty("view.type", "console"); }
}
