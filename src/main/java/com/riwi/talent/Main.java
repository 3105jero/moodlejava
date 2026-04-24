package com.riwi.talent;

import com.riwi.talent.config.AppConfig;
import com.riwi.talent.controller.EmployedController;
import com.riwi.talent.dao.EmployedDAO;
import com.riwi.talent.dao.impl.EmployedDAOImpl;
import com.riwi.talent.view.ConsoleView;
import com.riwi.talent.view.SwingView;
import com.riwi.talent.view.View;

/**
 * ============================================================
 *  RIWI Talent Manager — Punto de entrada principal
 * ============================================================
 *
 *  Arquitectura MVC implementada:
 *
 *  Main
 *   ├─► AppConfig           (config/)    ← lee database.properties + app.properties
 *   ├─► View factory        (view/)      ← elige ConsoleView o SwingView
 *   ├─► EmpleadoDAO         (dao/)       ← interfaz
 *   │     └─► EmpleadoDAOImpl (dao/impl/) ← CRUD con PreparedStatement
 *   │           └─► GenericDAOImpl        ← lógica CRUD genérica
 *   │                 └─► ConnectionManager (db/) ← gestión JDBC
 *   └─► EmpleadoController  (controller/) ← mediador MVC
 *
 *  Características Java 17 LTS aplicadas:
 *   • try-with-resources  → cierre automático de recursos JDBC  (Task 1)
 *   • PreparedStatement   → protección contra SQL Injection      (Task 2)
 *   • Patrón MVC          → separación de responsabilidades      (Task 3)
 *   • Records             → mapeo inmutable para reportes        (Task 4)
 *   • Text Blocks         → formato legible en BaseView          (Task 4)
 *   • var                 → inferencia de tipos local            (DAOImpl)
 *   • switch expressions  → flujo de módulos limpio             (este Main)
 */
public class Main {

    public static void main(String[] args) {

        AppConfig config = AppConfig.getInstance();

        // ── Factory: elige la vista según app.properties ──────────────────
        View view = createView(config.getViewType());

        // ── Inyección de dependencias ─────────────────────────────────────
        EmployedDAO        empleadoDAO  = new EmployedDAOImpl();
        EmployedController empleadoCtrl = new EmployedController(view, empleadoDAO);

        view.showMessage("Bienvenido a " + config.getAppName());

        // ── Menú principal de módulos ─────────────────────────────────────
        boolean running = true;
        while (running) {
            int modulo = view.getModuleChoice();
            switch (modulo) {
                case 1 -> empleadoCtrl.run();
                case 2 -> empleadoCtrl.showReport();
                case 3 -> running = false;
                default -> view.showError("Opción no válida.");
            }
        }

        view.showMessage("¡Hasta luego, Coder!");
    }

    // ── Vista factory: switch expression (Java 14+ / estable Java 17) ─────
    private static View createView(String type) {
        return switch (type.toLowerCase()) {
            case "swing" -> new SwingView();
            default      -> new ConsoleView();
        };
    }
}
