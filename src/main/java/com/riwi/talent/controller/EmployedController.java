package com.riwi.talent.controller;

import java.util.List;
import java.util.Optional;

import com.riwi.talent.dao.EmployedDAO;
import com.riwi.talent.model.entity.Employed;
import com.riwi.talent.model.entity.EmployedRecord;
import com.riwi.talent.view.View;

/**
 * TASK 3 — Controlador de Empleados (Capa Controller del patrón MVC).
 *
 * RESPONSABILIDADES:
 *   ✅ Recibir eventos de la Vista (datos del usuario)
 *   ✅ Validar y construir entidades antes de llamar al DAO
 *   ✅ Orquestar las operaciones del DAO
 *   ✅ Devolver resultados a la Vista
 *
 *   ❌ NO lee del teclado (eso es la Vista)
 *   ❌ NO imprime en consola (eso es la Vista)
 *   ❌ NO ejecuta SQL (eso es el DAO)
 *
 * Depende de la interfaz View (no de ConsoleView ni SwingView),
 * lo que permite cambiar la vista sin modificar el controlador.
 */
public class EmployedController {

    private final View       view;
    private final EmployedDAO dao;

    // Inyección de dependencias por constructor
    public EmployedController(View view, EmployedDAO dao) {
        this.view = view;
        this.dao  = dao;
    }

    // ── Menú del módulo ───────────────────────────────────────────────────
    public void run() {
        String[] opciones = {
            "Listar todos los Coders",
            "Buscar Coder por ID",
            "Registrar nuevo Coder",
            "Actualizar Coder",
            "Desactivar Coder",
            "Volver"
        };

        boolean running = true;
        while (running) {
            view.showMenu(opciones, "Gestión de Coders");
            int choice = view.getMenuChoice();

            switch (choice) {
                case 1 -> List();
                case 2 -> SearchId();
                case 3 -> NewCoder();
                case 4 -> UpdateCoder();
                case 5 -> DisabledCoder();
                case 6 -> running = false;
                default -> view.showError("Opción no válida.");
            }
        }
    }

    // ── Listar ────────────────────────────────────────────────────────────
    public void List() {
        List<Employed> lista = dao.findAll();
        if (lista.isEmpty()) {
            view.showMessage("No hay Coders registrados.");
        } else {
            view.showEmployed(lista);
        }
    }

    // ── Buscar por ID ─────────────────────────────────────────────────────
    public void SearchId() {
        String input = view.askInput("ID del Coder");
        try {
            int id = Integer.parseInt(input);
            Optional<Employed> opt = dao.findById(id);
            if (opt.isPresent()) {
                view.showEmpleado(opt.get());
            } else {
                view.showError("No existe un Coder con ID=" + id);
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido: " + input);
        }
    }

    // ── Crear ─────────────────────────────────────────────────────────────
    public void NewCoder() {
        String nombre   = view.askInput("Nombre");
        String apellido = view.askInput("Apellido");
        String email    = view.askInput("Email");
        String cohorte  = view.askInput("Cohorte");
        String stack    = view.askInput("Stack");

        if (nombre.isBlank() || apellido.isBlank() || email.isBlank()) {
            view.showError("Nombre, apellido y email son obligatorios.");
            return;
        }
        if (!email.contains("@")) {
            view.showError("El email no tiene formato válido.");
            return;
        }
        if (dao.existsByEmail(email)) {
            view.showError("Ya existe un Coder con ese email.");
            return;
        }

        Employed nuevo = new Employed(nombre, apellido, email, cohorte, stack);
        dao.save(nuevo);
        view.showMessage("Coder registrado con ID: " + nuevo.getId());
    }

    // ── Actualizar ────────────────────────────────────────────────────────
    public void UpdateCoder() {
        String input = view.askInput("ID del Coder a actualizar");
        try {
            int id = Integer.parseInt(input);
            Optional<Employed> opt = dao.findById(id);
            if (opt.isEmpty()) {
                view.showError("No existe un Coder con ID=" + id);
                return;
            }

            Employed e = opt.get();
            view.showEmpleado(e);

            String nombre   = view.askInput("Nuevo nombre   [" + e.getname()   + "]");
            String apellido = view.askInput("Nuevo apellido [" + e.getLastName() + "]");
            String email    = view.askInput("Nuevo email    [" + e.getEmail()    + "]");
            String cohorte  = view.askInput("Nueva cohorte  [" + e.getCohorte() + "]");
            String stack    = view.askInput("Nuevo stack    [" + e.getStack()    + "]");

            if (!nombre.isBlank())   e.setNombre(nombre);
            if (!apellido.isBlank()) e.setApellido(apellido);
            if (!email.isBlank())    e.setEmail(email);
            if (!cohorte.isBlank())  e.setCohorte(cohorte);
            if (!stack.isBlank())    e.setStack(stack);

            boolean ok = dao.update(e);
            view.showMessage(ok ? "Coder actualizado correctamente." : "No se pudo actualizar.");

        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        }
    }

    // ── Desactivar (eliminación lógica) ───────────────────────────────────
    public void DisabledCoder() {
        String input = view.askInput("ID del Coder a desactivar");
        try {
            int id = Integer.parseInt(input);
            Optional<Employed> opt = dao.findById(id);
            if (opt.isEmpty()) {
                view.showError("No existe un Coder con ID=" + id);
                return;
            }
            if (view.confirm("¿Confirmar desactivación del Coder " + id + "?")) {
                boolean ok = dao.deleteById(id);
                view.showMessage(ok ? "Coder desactivado." : "No se pudo desactivar.");
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        }
    }

    // ── Reporte (TASK 4) ──────────────────────────────────────────────────
    public void showReport() {
        List<EmployedRecord> reporte = dao.generarReporte();
        view.showReporte(reporte);
    }
}
