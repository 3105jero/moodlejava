import java.util.*;

/**

 
 * Java 8  → Streams, lambdas, removeIf, forEach básico.
 * Java 9  → List.of(), Map.of() — colecciones de fábrica inmutables.
 * Java 11 → var (inferencia de tipos), mejoras en streams.
 * Java 21 → SequencedCollections: getFirst(), getLast(), reversed().

 */
public class CorporateTalentHub {

    // ──────────────────────────────────────────────────────────────────
    //  MODELO: Empleado
    // ──────────────────────────────────────────────────────────────────
    static class Empleado {
        private final String id;
        private final String nombre;
        private final String tecnologia;
        private final String sede;
        private final double salario;
        private final int puntaje;   // 0-100

        public Empleado(String id, String nombre,
                        String tecnologia, String sede,
                        double salario, int puntaje) {
            this.id          = id;
            this.nombre      = nombre;
            this.tecnologia  = tecnologia;
            this.sede        = sede;
            this.salario     = salario;
            this.puntaje     = puntaje;
        }

        public String  getId()          { return id; }
        public String  getNombre()      { return nombre; }
        public String  getTecnologia()  { return tecnologia; }
        public String  getSede()        { return sede; }
        public double  getSalario()     { return salario; }
        public int     getPuntaje()     { return puntaje; }

        @Override
        public String toString() {
            return String.format(
                "Empleado{id='%s', nombre='%-20s', tech='%-10s', sede='%-12s', salario=$%,.0f, puntaje=%d}",
                id, nombre, tecnologia, sede, salario, puntaje);
        }
    }

    // ──────────────────────────────────────────────────────────────────
    //  UTILIDADES DE CONSOLA
    // ──────────────────────────────────────────────────────────────────
    static void titulo(String texto) {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("  " + texto);
        System.out.println("═".repeat(70));
    }

    static void subtitulo(String texto) {
        System.out.println("\n  ▶ " + texto);
        System.out.println("  " + "─".repeat(60));
    }

    static void info(String texto)   { System.out.println("  ℹ  " + texto); }
    static void ok(String texto)     { System.out.println("  ✔  " + texto); }
    static void warn(String texto)   { System.out.println("  ⚠  " + texto); }
    static void code(String texto)   { System.out.println("     » " + texto); }

    // ══════════════════════════════════════════════════════════════════
    //  TASK 1 — ArrayList<Empleado> + HashMap<String, Empleado>
    //           Migración a almacenamiento dinámico (Legacy Java 8/11)
    // ══════════════════════════════════════════════════════════════════
    static ArrayList<Empleado>       listaEmpleados = new ArrayList<>();
    static HashMap<String, Empleado> mapaEmpleados  = new HashMap<>();

    /** Agrega un empleado a ambas colecciones dinámicas. */
    static void agregarEmpleado(Empleado e) {
        listaEmpleados.add(e);
        mapaEmpleados.put(e.getId(), e);
        ok("Agregado → " + e);
    }

    /** Elimina un empleado por ID de ambas colecciones. */
    static void eliminarEmpleado(String id) {
        Empleado removido = mapaEmpleados.remove(id);
        if (removido != null) {
            listaEmpleados.remove(removido);
            warn("Eliminado → " + removido);
        } else {
            info("ID '" + id + "' no encontrado.");
        }
    }

    /** Búsqueda O(1) por ID usando el HashMap. */
    static void buscarPorId(String id) {
        Empleado e = mapaEmpleados.get(id);
        if (e != null) ok("Encontrado  → " + e);
        else           info("No existe empleado con ID: " + id);
    }

    static void task1() {
        titulo("TASK 1 — ArrayList + HashMap  (Legacy Java 8/11)");

        subtitulo("1.1 — Poblar colecciones dinámicas");
        // Antes (Semana 2): Empleado[] arreglo = new Empleado[10];  → FIJO
        // Ahora (Task 1):   ArrayList<Empleado> lista;              → ILIMITADO
        agregarEmpleado(new Empleado("E001", "Ana García",      "Java",       "Bogotá",     5_800_000, 92));
        agregarEmpleado(new Empleado("E002", "Carlos Méndez",   "Python",     "Medellín",   4_900_000, 78));
        agregarEmpleado(new Empleado("E003", "Lucía Romero",    "JavaScript", "Cali",       5_200_000, 85));
        agregarEmpleado(new Empleado("E004", "Diego Herrera",   "Java",       "Barranquilla",3_500_000, 55));
        agregarEmpleado(new Empleado("E005", "Valentina Cruz",  "Kotlin",     "Bogotá",     6_100_000, 94));
        agregarEmpleado(new Empleado("E006", "Miguel Torres",   "Go",         "Medellín",   5_400_000, 70));
        agregarEmpleado(new Empleado("E007", "Sofía Vargas",    "TypeScript", "Cali",       4_600_000, 42));
        agregarEmpleado(new Empleado("E008", "Andrés Patiño",   "Rust",       "Bogotá",     7_000_000, 98));

        subtitulo("1.2 — Listar todos los empleados (ArrayList)");
        listaEmpleados.forEach(e -> info(e.toString()));

        subtitulo("1.3 — Búsqueda instantánea por ID (HashMap O(1))");
        buscarPorId("E005");
        buscarPorId("E099");   // no existe

        subtitulo("1.4 — Eliminar empleado por ID");
        eliminarEmpleado("E007");
        info("Total después de eliminación: " + listaEmpleados.size() + " empleados");
    }

    // ══════════════════════════════════════════════════════════════════
    //  TASK 2 — List.of() y Map.of()  (Factory Methods — Java 9/11)
    // ══════════════════════════════════════════════════════════════════
    static void task2() {
        titulo("TASK 2 — List.of() y Map.of()  (Factory Methods — Java 9/11)");

        // ── List.of() ── Java 9+
        // SEGURIDAD vs ArrayList tradicional:
        //  • ArrayList permite null, List.of() lo rechaza en tiempo de ejecución.
        //  • ArrayList es mutable; cualquier código puede agregar/quitar elementos.
        //  • List.of() garantiza que NADIE puede modificar los datos de configuración
        //    una vez inicializados (principio de inmutabilidad / fail-fast).
        //  • LIMITACIÓN: no soportan .add(), .remove() ni .set() → UnsupportedOperationException.

        List<String> tecnologias = List.of(
            "Java", "Kotlin", "Python", "JavaScript", "TypeScript",
            "Go", "Rust", "Swift", "Dart", "C#"
        );

        List<String> sedes = List.of(
            "Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena"
        );

        // Map.of() — Java 9+: hasta 10 pares clave-valor directamente
        Map<String, String> regionesSede = Map.of(
            "Bogotá",       "Centro",
            "Medellín",     "Antioquia",
            "Cali",         "Valle del Cauca",
            "Barranquilla", "Atlántico",
            "Cartagena",    "Bolívar"
        );

        subtitulo("2.1 — Tecnologías disponibles (List.of — inmutable)");
        tecnologias.forEach(t -> info("  • " + t));

        subtitulo("2.2 — Sedes corporativas (List.of — inmutable)");
        sedes.forEach(s -> info("  • " + s + " → " + regionesSede.get(s)));

        subtitulo("2.3 — Demostración de inmutabilidad (¡protección en tiempo real!)");
        try {
            tecnologias.add("COBOL"); // Intentar mutar lista inmutable
        } catch (UnsupportedOperationException ex) {
            warn("List.of() es INMUTABLE — .add() lanza UnsupportedOperationException");
            code("Esta protección NO existe en: new ArrayList<>(...) ");
            code("Usar List.of() para configuración evita mutaciones accidentales.");
        }

        subtitulo("2.4 — ¿Cuándo usar cada uno?");
        info("List.of()  → datos de configuración, catálogos, constantes (NUNCA cambian).");
        info("ArrayList  → colecciones operativas que sí necesitan crecer/reducirse.");
    }

    // ══════════════════════════════════════════════════════════════════
    //  TASK 3 — SequencedCollections  (Java 21 LTS)
    // ══════════════════════════════════════════════════════════════════
    static void task3() {
        titulo("TASK 3 — SequencedCollections: getFirst / getLast / reversed  (Java 21)");

        // ── SINTAXIS LEGACY (Java 8/11) ── índices manuales, propensos a error
        subtitulo("3.1 — Acceso al primer y último empleado  [Legacy Java 8/11]");
        code("Primer empleado : lista.get(0)");
        code("Último empleado : lista.get(lista.size() - 1)   ← fácil olvidar el -1 → IndexOutOfBoundsException");

        Empleado primerLegacy = listaEmpleados.get(0);
        Empleado ultimoLegacy = listaEmpleados.get(listaEmpleados.size() - 1);
        info("Primero (Legacy): " + primerLegacy);
        info("Último  (Legacy): " + ultimoLegacy);

        // ── SINTAXIS MODERNA (Java 21) ── SequencedCollection interface
        //  MEJORAS vs Legacy:
        //  • getFirst() / getLast() son semánticamente claros: no hay "magia" de índices.
        //  • Si la lista está vacía, lanzan NoSuchElementException (más descriptivo).
        //  • reversed() devuelve una VISTA invertida sin copiar la lista (O(1) vs O(n)).
        //  • Reduce drásticamente los bugs de "off-by-one" (lista.size() vs lista.size()-1).
        subtitulo("3.2 — Acceso con SequencedCollections  [Java 21 — MODERNO]");
        code("Primer empleado : lista.getFirst()              ← claro, seguro, expresivo");
        code("Último empleado : lista.getLast()               ← sin aritmética de índices");
        code("Lista invertida : lista.reversed()              ← O(1), sin algoritmo manual");

        Empleado primerModerno = listaEmpleados.getFirst();
        Empleado ultimoModerno = listaEmpleados.getLast();
        info("Primero (Java 21): " + primerModerno);
        info("Último  (Java 21): " + ultimoModerno);

        subtitulo("3.3 — Lista en orden inverso  [reversed() — Java 21]");
        // reversed() retorna una SequencedCollection; es una vista, no una copia.
        var listaInvertida = listaEmpleados.reversed();
        listaInvertida.forEach(e -> info("  ← " + e.getNombre() + " | " + e.getTecnologia()));
        info("(reversed() es una VISTA — no modifica la lista original)");

        subtitulo("3.4 — Resumen de mejoras Java 21 en legibilidad");
        ok("getFirst()  →  elimina lista.get(0)              — sin magia de índice 0");
        ok("getLast()   →  elimina lista.get(size - 1)       — sin aritmética -1");
        ok("reversed()  →  elimina Collections.reverse(copy) — sin copia extra O(n)");
    }

    // ══════════════════════════════════════════════════════════════════
    //  TASK 4 — removeIf + var + Reporte Final  (Java 11+)
    // ══════════════════════════════════════════════════════════════════
    static void task4() {
        titulo("TASK 4 — removeIf + Inferencia var + Reporte Final  (Java 11+)");

        final int PUNTAJE_MINIMO = 65;

        subtitulo("4.1 — Estado antes del filtrado");
        info("Total de empleados: " + listaEmpleados.size());
        listaEmpleados.forEach(e ->
            info(String.format("  %-20s | Puntaje: %3d %s",
                e.getNombre(), e.getPuntaje(),
                e.getPuntaje() < PUNTAJE_MINIMO ? "← SERÁ ELIMINADO" : "✔")));

        subtitulo("4.2 — removeIf: eliminar coders con puntaje < " + PUNTAJE_MINIMO);
        // JAVA 8 (sin removeIf — forma manual):
        //   Iterator<Empleado> it = lista.iterator();
        //   while (it.hasNext()) {
        //       if (it.next().getPuntaje() < MIN) it.remove();
        //   }
        //
        // JAVA 8+ con removeIf (lambda — mucho más limpio):
        listaEmpleados.removeIf(e -> e.getPuntaje() < PUNTAJE_MINIMO);
        // Sincronizar el HashMap con los eliminados
        mapaEmpleados.entrySet().removeIf(entry -> entry.getValue().getPuntaje() < PUNTAJE_MINIMO);

        ok("removeIf ejecutado. Empleados restantes: " + listaEmpleados.size());

        subtitulo("4.3 — Inferencia de tipos con 'var'  (Java 11+)");
        // JAVA 8 — declaración explícita (verbosa):
        //   double totalSalarios = listaEmpleados.stream()
        //       .mapToDouble(Empleado::getSalario).sum();
        //   double promedioSalario = totalSalarios / listaEmpleados.size();
        //
        // JAVA 11+ — inferencia con var (más limpia, mismo bytecode):
        var totalSalarios   = listaEmpleados.stream()
                                .mapToDouble(Empleado::getSalario).sum();
        var promedioSalario = listaEmpleados.isEmpty() ? 0.0
                                : totalSalarios / listaEmpleados.size();
        var puntajePromedio = listaEmpleados.stream()
                                .mapToInt(Empleado::getPuntaje).average().orElse(0);

        // var en bucle — Java 11+
        // JAVA 8: for (Empleado e : listaEmpleados)
        // JAVA 11+: for (var e : listaEmpleados)
        code("var totalSalarios   = stream().mapToDouble(...).sum()   → tipo inferido: double");
        code("var promedioSalario = totalSalarios / size()            → tipo inferido: double");
        code("for (var e : listaEmpleados)                            → tipo inferido: Empleado");

        subtitulo("4.4 — Lista depurada de Coders calificados");
        for (var e : listaEmpleados) {
            info(String.format("  %-20s | Tech: %-10s | Sede: %-12s | Puntaje: %d | Salario: $%,.0f",
                e.getNombre(), e.getTecnologia(), e.getSede(), e.getPuntaje(), e.getSalario()));
        }

        subtitulo("4.5 — REPORTE FINAL CONSOLIDADO");
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────────────────┐");
        System.out.println("  │           CORPORATE TALENT HUB — REPORTE FINAL      │");
        System.out.println("  ├─────────────────────────────────────────────────────┤");
        System.out.printf ("  │  Total de coders calificados   : %-3d                │%n", listaEmpleados.size());
        System.out.printf ("  │  Puntaje mínimo aplicado       : %-3d                │%n", PUNTAJE_MINIMO);
        System.out.printf ("  │  Promedio de puntaje           : %5.1f              │%n", puntajePromedio);
        System.out.printf ("  │  Total nómina                  : $%,12.0f       │%n",  totalSalarios);
        System.out.printf ("  │  Salario promedio              : $%,12.0f       │%n",  promedioSalario);
        System.out.printf ("  │  Primer coder (getFirst)       : %-20s │%n",           listaEmpleados.getFirst().getNombre());
        System.out.printf ("  │  Último  coder (getLast)       : %-20s │%n",           listaEmpleados.getLast().getNombre());
        System.out.println("  └─────────────────────────────────────────────────────┘");
    }

    // ══════════════════════════════════════════════════════════════════
    //  MAIN — Orquesta las 4 tareas
    // ══════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║        CORPORATE TALENT HUB  —  Gestión Dinámica            ║");
        System.out.println("  ║        Evolución Java 8 → Java 11 → Java 21 (LTS)           ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════╝");

        task1();   // ArrayList + HashMap
        task2();   // List.of() + Map.of()
        task3();   // SequencedCollections (Java 21)
        task4();   // removeIf + var + Reporte

        System.out.println("\n  ✅  Corporate Talent Hub ejecutado exitosamente.\n");
    }
}
