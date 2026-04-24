package com.riwi.talent.view;

import java.util.List;
import java.util.Scanner;

import com.riwi.talent.model.entity.Employed;
import com.riwi.talent.model.entity.EmployedRecord;

/**
 * TASK 3 — Vista de consola (System.in / System.out).
 *
 * REGLA DE ORO: todo Scanner y todo System.out están ÚNICAMENTE aquí.
 * Ni el Controlador ni el DAO saben que existe una consola.
 */
public class ConsoleView extends BaseView {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void showMessage(String msg) {
        System.out.println("[INFO] " + msg);
    }

    @Override
    public void showError(String msg) {
        System.err.println("[ERROR] " + msg);
    }

    @Override
    public void showEmployed(List<Employed> empleados) {
        System.out.println("\n" + formatEmpleados(empleados));
    }

    @Override
    public void showEmpleado(Employed empleado) {
        System.out.println("\n" + formatEmpleado(empleado));
    }

    // TASK 4: el reporte ya viene formateado con Text Blocks desde BaseView
    @Override
    public void showReporte(List<EmployedRecord> reporte) {
        System.out.println("\n" + formatReporte(reporte));
    }

    @Override
    public String askInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    @Override
    public boolean confirm(String question) {
        System.out.print(question + " (s/n): ");
        String r = scanner.nextLine().trim().toLowerCase();
        return r.equals("s") || r.equals("si") || r.equals("sí");
    }

    @Override
    public void showMenu(String[] options, String title) {
        System.out.println("\n" + buildMenu(options, title));
    }

    @Override
    public int getMenuChoice() {
        System.out.print("Opción: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public int getModuleChoice() {
        String[] modulos = { "Gestión de Coders", "Reporte consolidado", "Salir" };
        showMenu(modulos, "RIWI Talent Manager");
        return getMenuChoice();
    }
}
