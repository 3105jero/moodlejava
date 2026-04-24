package com.riwi.talent.view;

import java.util.List;

import javax.swing.JOptionPane;

import com.riwi.talent.model.entity.Employed;
import com.riwi.talent.model.entity.EmployedRecord;

/**
 * TASK 3 — Vista basada en JOptionPane (Swing).
 * Reutiliza toda la lógica de formato de BaseView.
 * El Controlador no sabe si está hablando con consola o Swing.
 */
public class SwingView extends BaseView {

    @Override
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showEmployed(List<Employed> empleados) {
        JOptionPane.showMessageDialog(null,
                formatEmpleados(empleados), "Lista de Coders",
                JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void showEmpleado(Employed empleado) {
        JOptionPane.showMessageDialog(null,
                formatEmpleado(empleado), "Detalle del Coder",
                JOptionPane.PLAIN_MESSAGE);
    }

    // TASK 4: reporte con Text Blocks formateado en BaseView
    @Override
    public void showReporte(List<EmployedRecord> reporte) {
        JOptionPane.showMessageDialog(null,
                formatReporte(reporte), "Reporte Consolidado",
                JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public String askInput(String prompt) {
        String result = JOptionPane.showInputDialog(null, prompt);
        return result != null ? result.trim() : "";
    }

    @Override
    public boolean confirm(String question) {
        int r = JOptionPane.showConfirmDialog(null, question,
                "Confirmar", JOptionPane.YES_NO_OPTION);
        return r == JOptionPane.YES_OPTION;
    }

    @Override
    public void showMenu(String[] options, String title) {
        // En Swing el menú se muestra en getMenuChoice()
    }

    @Override
    public int getMenuChoice() {
        String[] opts = {
            "Listar todos los Coders",
            "Buscar Coder por ID",
            "Registrar nuevo Coder",
            "Actualizar Coder",
            "Desactivar Coder",
            "Volver"
        };
        Object sel = JOptionPane.showInputDialog(
                null, "Selecciona una opción:", "Gestión de Coders",
                JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
        if (sel == null) return 6;
        for (int i = 0; i < opts.length; i++) {
            if (opts[i].equals(sel)) return i + 1;
        }
        return -1;
    }

    @Override
    public int getModuleChoice() {
        String[] modulos = { "Gestión de Coders", "Reporte consolidado", "Salir" };
        Object sel = JOptionPane.showInputDialog(
                null, "¿Qué módulo deseas gestionar?", "RIWI Talent Manager",
                JOptionPane.PLAIN_MESSAGE, null, modulos, modulos[0]);
        if (sel == null) return 3;
        if ("Gestión de Coders".equals(sel))    return 1;
        if ("Reporte consolidado".equals(sel))  return 2;
        return 3;
    }
}
