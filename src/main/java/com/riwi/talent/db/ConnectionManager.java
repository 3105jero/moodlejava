package com.riwi.talent.db;

import com.riwi.talent.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ============================================================
 *  TASK 1 — Gestión de Conexiones y Recursos (Legacy vs Modern)
 * ============================================================
 *
 *  Singleton para gestión de conexiones JDBC.
 *  Lee las credenciales desde database.properties (via AppConfig),
 *  nunca desde strings hardcodeados en el código fuente.
 *
 *  En proyectos productivos se reemplaza por un pool de conexiones
 *  (HikariCP, c3p0) para mayor rendimiento y control de timeouts.
 *
 * ═══════════════════════════════════════════════════════════════
 *  LEGACY (Java 8 hacia atrás) — cierre manual con finally
 * ═══════════════════════════════════════════════════════════════
 *
 *  Connection conn = null;
 *  PreparedStatement ps = null;
 *  ResultSet rs = null;
 *  try {
 *      conn = DriverManager.getConnection(url, user, pass);
 *      ps   = conn.prepareStatement("SELECT * FROM empleados");
 *      rs   = ps.executeQuery();
 *      // ... procesar ...
 *  } catch (SQLException e) {
 *      e.printStackTrace();
 *  } finally {
 *      // ⚠️ PROBLEMA: el cierre es responsabilidad del programador.
 *      // Si se olvida un recurso, o si rs.close() lanza excepción,
 *      // ps y conn NUNCA se cierran → Memory Leak garantizado:
 *      //   • Conexiones abiertas → el servidor MySQL agota max_connections.
 *      //   • PreparedStatements abiertos → el servidor agota cursores.
 *      //   • ResultSets abiertos → locks sobre tablas, degradación de BD.
 *      try { if (rs   != null) rs.close();   } catch (SQLException e) { e.printStackTrace(); }
 *      try { if (ps   != null) ps.close();   } catch (SQLException e) { e.printStackTrace(); }
 *      try { if (conn != null) conn.close();  } catch (SQLException e) { e.printStackTrace(); }
 *      // Notar el boilerplate: 6 líneas solo para cerrar 3 recursos.
 *  }
 *
 * ═══════════════════════════════════════════════════════════════
 *  MODERN (Java 7+ / Java 17 LTS / Java 21 LTS) — try-with-resources
 * ═══════════════════════════════════════════════════════════════
 *
 *  try (Connection conn = DriverManager.getConnection(url, user, pass);
 *       PreparedStatement ps = conn.prepareStatement("SELECT * FROM empleados");
 *       ResultSet rs = ps.executeQuery()) {
 *
 *      // ... procesar ...
 *
 *  } catch (SQLException e) {
 *      throw new RuntimeException("Error de BD", e);
 *  }
 *  // ✅ Java cierra los recursos en orden INVERSO (rs → ps → conn).
 *  // ✅ El cierre ocurre aunque se lance cualquier excepción en el cuerpo.
 *  // ✅ Si close() también falla, la excepción queda como "suppressed"
 *  //    sin ocultar la excepción original (diagnóstico completo).
 *  // ✅ Cero boilerplate: el compilador garantiza el cierre.
 *
 * ═══════════════════════════════════════════════════════════════
 *  CÓMO try-with-resources PREVIENE MEMORY LEAKS
 * ═══════════════════════════════════════════════════════════════
 *  1. CIERRE GARANTIZADO : el bloque cierra los recursos en cualquier
 *     escenario (éxito, excepción, return anticipado).
 *  2. ORDEN CORRECTO     : cierre en orden inverso al de apertura,
 *     respetando dependencias (ResultSet depende de PreparedStatement,
 *     PreparedStatement depende de Connection).
 *  3. SUPPRESSED EXCEPTIONS: si el cuerpo y el cierre lanzan excepciones,
 *     Java adjunta la del cierre como suppressed → no se pierde información.
 *  4. INTERFAZ AutoCloseable: Connection, PreparedStatement y ResultSet
 *     implementan AutoCloseable, por eso son elegibles para este bloque.
 */
public class ConnectionManager {

    private static ConnectionManager instance;
    private final AppConfig config = AppConfig.getInstance();

    private ConnectionManager() {
        // Registrar el driver JDBC explícitamente (requerido en algunos entornos)
        try {
            Class.forName(config.getDbDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC no encontrado: " + config.getDbDriver(), e);
        }
    }

    /** Double-checked locking para Singleton thread-safe. */
    public static ConnectionManager getInstance() {
        if (instance == null) {
            synchronized (ConnectionManager.class) {
                if (instance == null) instance = new ConnectionManager();
            }
        }
        return instance;
    }

    /**
     * Abre y retorna una nueva conexión JDBC.
     *
     * DEBE usarse siempre dentro de un try-with-resources en la capa DAO:
     *
     *   try (Connection conn = ConnectionManager.getInstance().getConnection()) {
     *       // usar conn ...
     *   }
     *
     * @throws SQLException si las credenciales son incorrectas o el servidor no responde.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config.getDbUrl(),
                config.getDbUser(),
                config.getDbPassword());
    }
}
