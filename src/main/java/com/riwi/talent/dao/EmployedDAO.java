package com.riwi.talent.dao;

import java.util.List;

import com.riwi.talent.model.entity.Employed;
import com.riwi.talent.model.entity.EmployedRecord;

/**
 * TASK 2 — Interfaz EmpleadoDAO.
 * Extiende GenericDAO añadiendo operaciones específicas de Empleado.
 */
public interface EmployedDAO extends GenericDAO<Employed, Integer> {

    /** Busca coders cuyo nombre contiene el texto dado. */
    List<Employed> findByNombre(String nombre);

    /** Verifica si ya existe un coder con ese email. */
    boolean existsByEmail(String email);

    /**
     * TASK 4 — Genera el reporte consolidado mapeando a Records.
     * Usa un SELECT con formato de fecha para el reporte final.
     */
    List<EmployedRecord> generarReporte();
}
