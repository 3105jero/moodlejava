package com.riwi.talent.view;

import java.util.List;

import com.riwi.talent.model.entity.Employed;
import com.riwi.talent.model.entity.EmployedRecord;

/**
 * TASK 3 — Contrato que toda Vista debe cumplir.
 * El Controlador solo conoce esta interfaz, nunca ConsoleView ni SwingView.
 * Esto permite cambiar la vista sin tocar el controlador (Open/Closed Principle).
 */
public interface View {

    void    showMessage(String msg);
    void    showError(String msg);

    // ── Empleados ──
    void    showEmployed(List<Employed> empleados);
    void    showEmpleado(Employed empleado);
    void    showReporte(List<EmployedRecord> reporte);

    // ── Interacción ──
    String  askInput(String prompt);
    boolean confirm(String question);
    void    showMenu(String[] options, String title);
    int     getMenuChoice();

    // ── Menú principal ──
    int     getModuleChoice();
}
