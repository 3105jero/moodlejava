package com.riwi.talent.model.entity;

/**
 * ============================================================
 *  TASK 4 — EmpleadoRecord (Java 16+ estable / Java 17 LTS)
 * ============================================================
 *
 *  Un Record es una clase especial inmutable. Con una sola línea
 *  el compilador genera automáticamente:
 *    ✅ Campos privados finales
 *    ✅ Constructor canónico
 *    ✅ Accessors: id(), nombre(), email()...  (sin "get")
 *    ✅ equals(), hashCode() y toString() correctos
 *
 * ─── Record vs POJO (Java 8) para mapear SELECT ──────────────
 *
 *  | Criterio            | POJO (Java 8)             | Record (Java 17+)         |
 *  |---------------------|---------------------------|---------------------------|
 *  | Líneas de código    | ~55 (getters + setters)   | ~10                       |
 *  | Mutabilidad         | Mutable → riesgo de bugs  | Inmutable → thread-safe   |
 *  | equals/hashCode     | Manual y propenso a errores| Automático y correcto     |
 *  | Mantenimiento       | Cambiar campo = 3 ediciones| Cambiar campo = 1 edición |
 *  | Intención de diseño | Implícita                 | Explícita: "es solo datos"|
 *
 *  CUÁNDO usar Record vs POJO en esta app:
 *   • Empleado (POJO)     → INSERT / UPDATE / DELETE  (necesita setters)
 *   • EmpleadoRecord      → SELECT / reporte final    (solo lectura, seguro)
 *
 *  La combinación Records + JDBC moderno (try-with-resources) elimina
 *  el boilerplate de Java 8 en ambas capas: menos código, menos bugs.
 */
public record EmployedRecord(
        int     id,
        String  nombre,
        String  apellido,
        String  email,
        String  cohorte,
        String  stack,
        boolean activo,
        String  fechaIngreso   // ya formateado para mostrar en reporte
) {
    /** Nombre completo para mostrar en pantalla. */
    public String fullname() {
        return nombre + " " + apellido;
    }

    /** Estado legible para el reporte. */
    public String estadoTexto() {
        return activo ? "ACTIVO ✓" : "INACTIVO ✗";
    }
}
