package com.riwi.talent.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.riwi.talent.dao.EmployedDAO;
import com.riwi.talent.model.entity.Employed;
import com.riwi.talent.model.entity.EmployedRecord;

/**
 * TASK 2 — Implementación concreta de EmpleadoDAO.
 * TASK 4 — generarReporte() mapea a EmpleadoRecord.
 *
 * Hereda el CRUD genérico de GenericDAOImpl y solo define:
 *   • Las queries SQL de empleados
 *   • El mapeo ResultSet → Empleado
 *   • Los métodos específicos: findByNombre, existsByEmail, generarReporte
 */
public class EmployedDAOImpl extends GenericDAOImpl<Employed, Integer>
        implements EmployedDAO {

    // ── Queries SQL centralizadas ─────────────────────────────────────────
    private static final String INSERT =
            "INSERT INTO empleados (nombre, apellido, email, cohorte, stack, activo, fecha_ingreso) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE empleados SET nombre=?, apellido=?, email=?, cohorte=?, stack=?, activo=? " +
            "WHERE id=?";

    // Eliminación LÓGICA: conserva el historial marcando activo=0
    private static final String DELETE =
            "UPDATE empleados SET activo=0 WHERE id=?";

    private static final String FIND_BY_ID =
            "SELECT id, nombre, apellido, email, cohorte, stack, activo, fecha_ingreso " +
            "FROM empleados WHERE id=?";

    private static final String FIND_ALL =
            "SELECT id, nombre, apellido, email, cohorte, stack, activo, fecha_ingreso " +
            "FROM empleados ORDER BY id";

    private static final String FIND_BY_NOMBRE =
            "SELECT id, nombre, apellido, email, cohorte, stack, activo, fecha_ingreso " +
            "FROM empleados WHERE nombre LIKE ?";

    private static final String EXISTS_EMAIL =
            "SELECT COUNT(*) FROM empleados WHERE email=?";

    // SELECT con formato de fecha para el reporte (TASK 4)
    private static final String REPORTE =
            "SELECT id, nombre, apellido, email, cohorte, stack, activo, " +
            "DATE_FORMAT(fecha_ingreso, '%d/%m/%Y') AS fecha_formato " +
            "FROM empleados ORDER BY cohorte, apellido";

    // ── Mapeo ResultSet → Empleado ────────────────────────────────────────
    @Override
    protected Employed mapRow(ResultSet rs) throws SQLException {
        return new Employed(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("email"),
                rs.getString("cohorte"),
                rs.getString("stack"),
                rs.getBoolean("activo"),
                rs.getDate("fecha_ingreso").toLocalDate()
        );
    }

    // ── Providers de SQL ─────────────────────────────────────────────────
    @Override protected String getInsertSQL()   { return INSERT; }
    @Override protected String getUpdateSQL()   { return UPDATE; }
    @Override protected String getDeleteSQL()   { return DELETE; }
    @Override protected String getFindByIdSQL() { return FIND_BY_ID; }
    @Override protected String getFindAllSQL()  { return FIND_ALL; }

    // ── Asignación de parámetros ─────────────────────────────────────────
    @Override
    protected void setInsertParams(PreparedStatement ps, Employed e) throws SQLException {
        ps.setString(1, e.getname());
        ps.setString(2, e.getLastName());
        ps.setString(3, e.getEmail());
        ps.setString(4, e.getCohorte());
        ps.setString(5, e.getStack());
        ps.setBoolean(6, e.isActivo());
        ps.setDate(7, Date.valueOf(
                e.getFechaIngreso() != null ? e.getFechaIngreso() : LocalDate.now()));
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, Employed e) throws SQLException {
        ps.setString(1, e.getname());
        ps.setString(2, e.getLastName());
        ps.setString(3, e.getEmail());
        ps.setString(4, e.getCohorte());
        ps.setString(5, e.getStack());
        ps.setBoolean(6, e.isActivo());
        ps.setInt(7, e.getId());
    }

    @Override
    protected void setDeleteParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    protected void setFindByIdParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }

    // ── save: INSERT y retorna el objeto con el ID generado ───────────────
    @Override
    public Employed save(Employed e) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     INSERT, Statement.RETURN_GENERATED_KEYS)) {

            setInsertParams(ps, e);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.setId(keys.getInt(1));
            }
            return e;
        } catch (SQLException ex) {
            throw new RuntimeException("Error en save(Empleado)", ex);
        }
    }

    // ── Métodos específicos de EmpleadoDAO ────────────────────────────────

    @Override
    public List<Employed> findByNombre(String nombre) {
        List<Employed> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_NOMBRE)) {

            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByNombre", e);
        }
        return list;
    }

    @Override
    public boolean existsByEmail(String email) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_EMAIL)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en existsByEmail", e);
        }
    }

    /**
     * TASK 4 — Mapea resultados del SELECT complejo a EmpleadoRecord (inmutable).
     *
     * Ventaja de usar Record aquí vs POJO:
     * La lista retornada es de solo lectura por construcción. El Controlador
     * y la Vista no pueden modificar accidentalmente los datos del reporte,
     * algo que sí era posible (y fuente de bugs) con POJOs mutables en Java 8.
     */
    @Override
    public List<EmployedRecord> generarReporte() {
        List<EmployedRecord> reporte = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(REPORTE);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // var: inferencia de tipo local (Java 10+, estable Java 17 LTS)
                var record = new EmployedRecord(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getString("cohorte"),
                        rs.getString("stack"),
                        rs.getBoolean("activo"),
                        rs.getString("fecha_formato")
                );
                reporte.add(record);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en generarReporte", e);
        }
        return reporte;
    }
}
