package com.riwi.talent.view;

import java.util.List;

import com.riwi.talent.model.entity.Employed;
import com.riwi.talent.model.entity.EmployedRecord;

/**
 * TASK 3 — Comportamiento común a todas las vistas.
 * Evita duplicar lógica de formato entre ConsoleView y SwingView.
 */
public abstract class BaseView implements View {

    // ── Formato de tabla de empleados ─────────────────────────────────────
    protected String formatEmpleados(List<Employed> empleados) {
        if (empleados.isEmpty()) return "(Sin Coders registrados)";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s %-20s %-30s %-13s %-18s %-7s%n",
                "ID", "NOMBRE COMPLETO", "EMAIL", "COHORTE", "STACK", "ESTADO"));
        sb.append("─".repeat(95)).append("\n");
        for (Employed e : empleados) {
            sb.append(String.format("%-4d %-20s %-30s %-13s %-18s %-7s%n",
                    e.getId(),
                    e.getname() + " " + e.getLastName(),
                    e.getEmail(),
                    e.getCohorte(),
                    e.getStack(),
                    e.isActivo() ? "ACTIVO" : "INACT."));
        }
        return sb.toString();
    }

    protected String formatEmpleado(Employed e) {
        return String.format(
                "ID      : %d%nNombre  : %s %s%nEmail   : %s%nCohorte : %s%nStack   : %s%nEstado  : %s",
                e.getId(), e.getname(), e.getLastName(),
                e.getEmail(), e.getCohorte(), e.getStack(),
                e.isActivo() ? "ACTIVO" : "INACTIVO");
    }

    // ── TASK 4: Formato del reporte usando Text Blocks ────────────────────
    /**
     * Text Blocks (Java 15+ / estable Java 17 LTS):
     * Permiten definir texto multilínea sin concatenaciones ni \n manuales.
     * En Java 8 esto requería StringBuilder + decenas de append().
     */
    protected String formatReporte(List<EmployedRecord> reporte) {
        if (reporte.isEmpty()) return "(Sin datos para el reporte)";

        long activos   = reporte.stream().filter(EmployedRecord::activo).count();
        long inactivos = reporte.size() - activos;

        // Text Block para el encabezado del reporte
        var sb = new StringBuilder("""
                ╔══════════════════════════════════════════════════════════╗
                ║        📊 REPORTE CONSOLIDADO — RIWI TALENT MANAGER     ║
                ╚══════════════════════════════════════════════════════════╝
                """);

        sb.append(String.format("""
                  Total de Coders : %d
                  Activos         : %d
                  Inactivos       : %d
                %n""", reporte.size(), activos, inactivos));

        sb.append("  ─────────────────────────────────────────────────────\n");

        for (EmployedRecord r : reporte) {
            // Text Block por registro — legible, sin concatenaciones
            sb.append(String.format("""
                      ID      : %d
                      Coder   : %s
                      Email   : %s
                      Cohorte : %s  |  Stack: %s
                      Ingreso : %s
                      Estado  : %s
                    %n""",
                    r.id(), r.fullname(), r.email(),
                    r.cohorte(), r.stack(),
                    r.fechaIngreso(), r.estadoTexto()));
        }

        sb.append("  ─────────────────────────────────────────────────────\n");
        sb.append("  Fin del reporte\n");
        return sb.toString();
    }

    protected String buildMenu(String[] options, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══ ").append(title).append(" ═══╗\n");
        for (int i = 0; i < options.length; i++) {
            sb.append(String.format("  %d. %s%n", i + 1, options[i]));
        }
        sb.append("╚").append("═".repeat(title.length() + 8)).append("╝");
        return sb.toString();
    }
}
