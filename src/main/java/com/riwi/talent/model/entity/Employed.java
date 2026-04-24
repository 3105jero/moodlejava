package com.riwi.talent.model.entity;

import java.time.LocalDate;

/**
 * Entidad Empleado — POJO mutable (estilo clásico Java 8).
 * Se usa en operaciones de escritura: INSERT, UPDATE, DELETE.
 * Para lectura/reporte se usa EmpleadoRecord (Task 4).
 */
public class Employed {

    private int       id;
    private String    nombre;
    private String    apellido;
    private String    email;
    private String    cohorte;
    private String    stack;
    private boolean   activo;
    private LocalDate fechaIngreso;

    public Employed() {}

    public Employed(String nombre, String apellido, String email,
                    String cohorte, String stack) {
        this.nombre       = nombre;
        this.apellido     = apellido;
        this.email        = email;
        this.cohorte      = cohorte;
        this.stack        = stack;
        this.activo       = true;
        this.fechaIngreso = LocalDate.now();
    }

    public Employed(int id, String nombre, String apellido, String email,
                    String cohorte, String stack, boolean activo, LocalDate fechaIngreso) {
        this.id           = id;
        this.nombre       = nombre;
        this.apellido     = apellido;
        this.email        = email;
        this.cohorte      = cohorte;
        this.stack        = stack;
        this.activo       = activo;
        this.fechaIngreso = fechaIngreso;
    }

    // ── Getters / Setters ────────────────────────────────────────────────
    public int       getId()                            { return id; }
    public void      setId(int id)                      { this.id = id; }

    public String    getname()                        { return nombre; }
    public void      setNombre(String nombre)           { this.nombre = nombre; }

    public String    getLastName()                      { return apellido; }
    public void      setApellido(String apellido)       { this.apellido = apellido; }

    public String    getEmail()                         { return email; }
    public void      setEmail(String email)             { this.email = email; }

    public String    getCohorte()                       { return cohorte; }
    public void      setCohorte(String cohorte)         { this.cohorte = cohorte; }

    public String    getStack()                         { return stack; }
    public void      setStack(String stack)             { this.stack = stack; }

    public boolean   isActivo()                         { return activo; }
    public void      setActivo(boolean activo)          { this.activo = activo; }

    public LocalDate getFechaIngreso()                  { return fechaIngreso; }
    public void      setFechaIngreso(LocalDate f)       { this.fechaIngreso = f; }

    @Override
    public String toString() {
        return String.format("Empleado{id=%d, nombre='%s %s', email='%s', cohorte='%s', stack='%s', activo=%b}",
                id, nombre, apellido, email, cohorte, stack, activo);
    }
}
