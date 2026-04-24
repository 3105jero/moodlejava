package com.riwi.talent.dao.impl;

import com.riwi.talent.dao.GenericDAO;
import com.riwi.talent.db.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 *  TASK 1 + TASK 2 — Implementación abstracta genérica del DAO
 * ============================================================
 *
 *  Implementa el CRUD una sola vez para cualquier entidad T.
 *  Las subclases concretas (EmpleadoDAOImpl) solo deben definir:
 *    • mapRow()          → cómo convertir una fila ResultSet en objeto T
 *    • getXxxSQL()       → las queries SQL propias de la entidad
 *    • setXxxParams()    → cómo asignar los ? de cada PreparedStatement
 *
 * ─── SEGURIDAD: PreparedStatement vs SQL Injection ───────────
 *
 *  ❌ VULNERABLE (concatenación directa):
 *     String sql = "SELECT * FROM empleados WHERE email = '" + email + "'";
 *     // Si email = "' OR '1'='1", el atacante obtiene todos los registros.
 *     // Si email = "'; DROP TABLE empleados; --", se elimina la tabla.
 *
 *  ✅ SEGURO (PreparedStatement con placeholders ?):
 *     String sql = "SELECT * FROM empleados WHERE email = ?";
 *     ps.setString(1, email);
 *     // El driver JDBC trata el valor como dato literal, NUNCA como SQL.
 *     // Caracteres especiales y comillas se escapan automáticamente.
 *
 *  REGLA: el símbolo ? es el ÚNICO modo de pasar valores a SQL.
 *  Esta clase lo aplica en el 100% de las operaciones CRUD.
 *
 * ─── TASK 1: try-with-resources en cada operación ────────────
 *
 *  Todos los métodos CRUD declaran Connection y PreparedStatement
 *  (y ResultSet cuando aplica) en el bloque try-with-resources.
 *  Java garantiza el cierre en orden inverso (ResultSet → PS → Connection)
 *  sin importar si se lanza excepción o no → cero Memory Leaks.
 */
public abstract class GenericDAOImpl<T, ID> implements GenericDAO<T, ID> {

    protected final ConnectionManager cm = ConnectionManager.getInstance();

    // ── Métodos abstractos que cada subclase debe implementar ─────────────
    protected abstract T      mapRow(ResultSet rs)                              throws SQLException;
    protected abstract String getInsertSQL();
    protected abstract String getUpdateSQL();
    protected abstract String getDeleteSQL();
    protected abstract String getFindByIdSQL();
    protected abstract String getFindAllSQL();
    protected abstract void   setInsertParams(PreparedStatement ps, T entity)  throws SQLException;
    protected abstract void   setUpdateParams(PreparedStatement ps, T entity)  throws SQLException;
    protected abstract void   setDeleteParam(PreparedStatement ps, ID id)      throws SQLException;
    protected abstract void   setFindByIdParam(PreparedStatement ps, ID id)    throws SQLException;

    // ── findById ──────────────────────────────────────────────────────────
    @Override
    public Optional<T> findById(ID id) {
        // MODERN: try-with-resources — Connection, PreparedStatement y ResultSet
        // se cierran automáticamente al salir del bloque (con o sin excepción).
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(getFindByIdSQL())) {

            setFindByIdParam(ps, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findById", e);
        }
    }

    // ── findAll ───────────────────────────────────────────────────────────
    @Override
    public List<T> findAll() {
        List<T> list = new ArrayList<>();
        // Los tres recursos JDBC en un solo try-with-resources:
        // ResultSet se cierra antes de PreparedStatement, que se cierra antes de Connection.
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(getFindAllSQL());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error en findAll", e);
        }
        return list;
    }

    // ── update ────────────────────────────────────────────────────────────
    @Override
    public boolean update(T entity) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(getUpdateSQL())) {

            setUpdateParams(ps, entity);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en update", e);
        }
    }

    // ── deleteById ────────────────────────────────────────────────────────
    @Override
    public boolean deleteById(ID id) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(getDeleteSQL())) {

            setDeleteParam(ps, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en deleteById", e);
        }
    }
}
