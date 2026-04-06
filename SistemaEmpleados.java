import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * ============================================================
 *  SISTEMA DE GESTIÓN DE CODERS - EVOLUCIÓN JAVA 8 → 17/21
 * ============================================================
 * Demuestra la evolución sintáctica entre versiones LTS de Java:
 * - Java 8  : switch clásico, tipos explícitos, mensajes de error básicos
 * - Java 11 : inferencia de tipos con var
 * - Java 17/21: switch expressions con ->, NPE mejoradas, sealed classes
 * ============================================================
 */
public class SistemaEmpleados {

    // ─────────────────────────────────────────────────────────
    //  CONSTANTES DEL SISTEMA
    // ─────────────────────────────────────────────────────────
    static final int MAX_EMPLEADOS  = 3;
    static final int TRIMESTRES     = 3;
    static final double NOTA_MIN    = 0.0;
    static final double NOTA_MAX    = 100.0;
    static final double UMBRAL_PROM = 70.0;  // Mínimo para promoción

    // ─────────────────────────────────────────────────────────
    //  PUNTO DE ENTRADA
    // ─────────────────────────────────────────────────────────
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Arreglos de soporte para los empleados registrados
        String[] nombres        = new String[MAX_EMPLEADOS];
        int[]    edades         = new int[MAX_EMPLEADOS];
        double[] salarios       = new double[MAX_EMPLEADOS];
        int      totalEmpleados = 0;

        /*
         * ══════════════════════════════════════════════════════════
         * TASK 2 – BUCLE PRINCIPAL do-while con Scanner
         * ══════════════════════════════════════════════════════════
         * El do-while garantiza que el menú se muestre al menos una
         * vez, manteniendo el sistema activo hasta que el usuario
         * elija salir (opción 5).
         */
        int opcion;
        do {
            mostrarMenu();   // Imprime el menú por consola

            // ──────────────────────────────────────────────────────
            // TASK 4 – try-catch: robustez ante entradas inválidas
            // ──────────────────────────────────────────────────────
            try {
                /*
                 * TASK 2 – var (Java 11+) vs tipo explícito (Java 8)
                 *
                 * Java 8  (explícito): int opcionLeida = scanner.nextInt();
                 * Java 11+(inferido) : var opcionLeida  = scanner.nextInt();
                 *
                 * El compilador infiere que opcionLeida es int porque
                 * scanner.nextInt() devuelve int. var NO es tipado dinámico;
                 * es azúcar sintáctico en tiempo de compilación.
                 */
                var opcionLeida = scanner.nextInt();   // Java 11+ – var
                scanner.nextLine();                    // Limpiar buffer

                opcion = opcionLeida;

                // ── TASK 1 – SWITCH CLÁSICO (Java 8) ──────────────
                /*
                 * COMPARATIVA: switch clásico vs switch expression
                 *
                 * ⚠ RIESGO JAVA 8 – "Fall-through":
                 *   Si se olvida el 'break', la ejecución cae al
                 *   siguiente case sin condición.  Ejemplo:
                 *     case 1:          ← sin break → ejecuta también case 2
                 *     case 2: ...
                 *   Esto es fuente de bugs silenciosos y difíciles
                 *   de detectar en código legacy.
                 *
                 * ✅ SEGURIDAD JAVA 17/21 – Switch Expression (->):
                 *   Cada rama con -> es un bloque independiente.
                 *   El fall-through es imposible por diseño del lenguaje.
                 *   Además es una expresión (puede asignarse a variable)
                 *   y el compilador exige exhaustividad en enums.
                 */
                switch (opcion) {                              // Java 8 – switch clásico
                    case 1:
                        System.out.println("\n[ Registrar nuevo Coder ]");
                        if (totalEmpleados >= MAX_EMPLEADOS) {
                            System.out.println("⚠ Capacidad máxima alcanzada (" + MAX_EMPLEADOS + " empleados).");
                            break;
                        }
                        totalEmpleados = registrarEmpleado(
                                scanner, nombres, edades, salarios, totalEmpleados);
                        break;                                 // ← obligatorio en Java 8

                    case 2:
                        System.out.println("\n[ Listar Coders registrados ]");
                        listarEmpleados(nombres, edades, salarios, totalEmpleados);
                        break;

                    case 3:
                        System.out.println("\n[ Matriz de desempeño y promedios ]");
                        if (totalEmpleados == 0) {
                            System.out.println("⚠ No hay empleados registrados.");
                        } else {
                            procesarDesempeno(scanner, nombres, totalEmpleados);
                        }
                        break;

                    case 4:
                        System.out.println("\n[ Categoría salarial (Switch Expression Java 17/21) ]");
                        mostrarCategoriasRegistradas(nombres, salarios, totalEmpleados);
                        break;

                    case 5:
                        System.out.println("\n✅ Saliendo del sistema. ¡Hasta pronto!");
                        break;

                    default:
                        System.out.println("⚠ Opción inválida. Ingrese un número entre 1 y 5.");
                        break;
                }

            } catch (InputMismatchException e) {
                /*
                 * TASK 4 – Captura de InputMismatchException
                 *
                 * Se dispara cuando el usuario escribe texto donde
                 * el programa espera un número (p.ej. "abc" en el menú).
                 *
                 * ANÁLISIS LTS – Evolución de mensajes de error:
                 *
                 * Java 8:
                 *   El stack trace mostraba mensajes genéricos como
                 *   "null" (sin mensaje detallado) en muchas excepciones,
                 *   incluida NullPointerException.  El diagnóstico
                 *   dependía únicamente de la línea del error.
                 *
                 * Java 14 (JEP 358) adoptado en Java 17/21:
                 *   Se introdujeron "Helpful NullPointerExceptions".
                 *   El JVM genera mensajes descriptivos que indican
                 *   EXACTAMENTE qué variable era null y en qué operación.
                 *   Ejemplo Java 17+:
                 *     Cannot invoke "String.length()" because "nombre" is null
                 *   Ejemplo Java 8:
                 *     NullPointerException (sin mensaje adicional)
                 *
                 *   Java 21 extiende esto a más tipos de excepciones
                 *   con el patrón de mensajes contextuales del JVM,
                 *   reduciendo drásticamente el tiempo de diagnóstico.
                 */
                System.out.println("❌ Error: entrada no válida. Ingrese solo números.");
                scanner.nextLine();  // Limpiar el token inválido del buffer
                opcion = 0;          // Forzar re-display del menú
            }

        } while (opcion != 5);

        scanner.close();
    }

    // ─────────────────────────────────────────────────────────
    //  TASK 1 – MENÚ PRINCIPAL (impresión por consola)
    // ─────────────────────────────────────────────────────────
    /**
     * Imprime el menú principal del sistema.
     * El control de flujo lo maneja el switch clásico en main().
     */
    static void mostrarMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║    SISTEMA DE GESTIÓN DE CODERS      ║");
        System.out.println("║         Java 8 → 17/21 LTS           ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Registrar Coder                  ║");
        System.out.println("║  2. Listar Coders                    ║");
        System.out.println("║  3. Matriz de Desempeño              ║");
        System.out.println("║  4. Categoría Salarial               ║");
        System.out.println("║  5. Salir                            ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    // ─────────────────────────────────────────────────────────
    //  TASK 2 – REGISTRO DE EMPLEADO con var y validación if/else
    // ─────────────────────────────────────────────────────────
    /**
     * Captura y valida los datos de un nuevo Coder.
     * Usa var (Java 11+) para variables locales de soporte.
     *
     * @return índice actualizado de totalEmpleados
     */
    static int registrarEmpleado(Scanner sc,
                                  String[] nombres,
                                  int[]    edades,
                                  double[] salarios,
                                  int      idx) {
        try {
            // ── var (Java 11+) vs explícito (Java 8) ────────────
            // Java 8  : String nombreIngresado = sc.nextLine();
            // Java 11+: var   nombreIngresado  = sc.nextLine();
            System.out.print("Nombre del Coder: ");
            var nombreIngresado = sc.nextLine();          // var → String

            // if/else: validación de rango para edad (byte: 0-127 práctico: 18-65)
            System.out.print("Edad (18-65): ");
            var edadIngresada = sc.nextInt();             // var → int
            sc.nextLine();

            if (edadIngresada < 18 || edadIngresada > 65) {
                System.out.println("⚠ Edad fuera de rango (18-65). Registro cancelado.");
                return idx;
            }

            // if/else: validación de rango para salario (double: > 0)
            System.out.print("Salario mensual (> 0): ");
            var salarioIngresado = sc.nextDouble();       // var → double
            sc.nextLine();

            if (salarioIngresado <= 0.0) {
                System.out.println("⚠ El salario debe ser mayor a 0. Registro cancelado.");
                return idx;
            }

            // Guardar datos validados
            nombres[idx]  = nombreIngresado;
            edades[idx]   = edadIngresada;
            salarios[idx] = salarioIngresado;

            // TASK 1 – Switch Expression (Java 17/21) en uso inmediato
            var categoria = obtenerCategoriaSalarial(salarioIngresado);  // var → String

            System.out.println("\n✅ Coder registrado exitosamente.");
            System.out.println("   Nombre   : " + nombreIngresado);
            System.out.println("   Edad     : " + edadIngresada);
            System.out.printf ("   Salario  : $%.2f%n", salarioIngresado);
            System.out.println("   Categoría: " + categoria);

            return idx + 1;

        } catch (InputMismatchException e) {
            System.out.println("❌ Dato inválido durante el registro. Operación cancelada.");
            sc.nextLine();
            return idx;
        }
    }

    // ─────────────────────────────────────────────────────────
    //  TASK 1 – SWITCH EXPRESSION (Java 17/21) con sintaxis ->
    // ─────────────────────────────────────────────────────────
    /**
     * Determina la categoría salarial de un Coder.
     *
     * USA: Switch Expression (Java 14 preview → estable en Java 17/21)
     *
     * COMPARATIVA:
     *
     * Java 8 (switch clásico con fall-through peligroso):
     * ─────────────────────────────────────────────────
     * String cat;
     * int nivel = (int)(salario / 1_000_000);
     * switch (nivel) {
     *     case 0:  cat = "Junior";   break;   // ← break obligatorio
     *     case 1:  cat = "Mid";      break;   // ← sin break → fall-through
     *     case 2:  cat = "Senior";   break;
     *     default: cat = "Lead";
     * }
     *
     * Java 17/21 (Switch Expression con ->):
     * ──────────────────────────────────────
     * String cat = switch (nivel) {
     *     case 0  -> "Junior";   // Sin break, sin fall-through posible
     *     case 1  -> "Mid";      // Cada rama es independiente por diseño
     *     case 2  -> "Senior";
     *     default -> "Lead";
     * };
     *
     * VENTAJAS Java 17/21:
     * ✅ Sin fall-through: imposible por diseño del lenguaje
     * ✅ Es una expresión: puede asignarse directamente a variable
     * ✅ Más conciso: sin break, sin llaves innecesarias
     * ✅ Exhaustividad: compilador exige cubrir todos los casos en enums
     */
    static String obtenerCategoriaSalarial(double salario) {
        int nivel = (int) (salario / 1_000_000);  // Nivel basado en millones COP

        // Switch Expression (Java 17/21) – sintaxis de flecha ->
        return switch (nivel) {
            case 0  -> "🟢 Junior  (< $1.000.000)";
            case 1  -> "🔵 Mid     ($1M - $1.99M)";
            case 2  -> "🟠 Senior  ($2M - $2.99M)";
            case 3  -> "🔴 Lead    ($3M - $3.99M)";
            default -> "⭐ Principal (≥ $4.000.000)";
        };
    }

    // ─────────────────────────────────────────────────────────
    //  LISTAR EMPLEADOS
    // ─────────────────────────────────────────────────────────
    static void listarEmpleados(String[] nombres, int[] edades,
                                 double[] salarios, int total) {
        if (total == 0) {
            System.out.println("⚠ No hay Coders registrados aún.");
            return;
        }
        System.out.println("\n┌─────┬──────────────────┬──────┬─────────────────┐");
        System.out.println("│ #   │ Nombre           │ Edad │ Salario         │");
        System.out.println("├─────┼──────────────────┼──────┼─────────────────┤");
        for (int i = 0; i < total; i++) {
            System.out.printf("│ %-3d │ %-16s │ %-4d │ $%-14.2f │%n",
                    i + 1, nombres[i], edades[i], salarios[i]);
        }
        System.out.println("└─────┴──────────────────┴──────┴─────────────────┘");
    }

    // ─────────────────────────────────────────────────────────
    //  MOSTRAR CATEGORÍAS DE TODOS LOS EMPLEADOS
    // ─────────────────────────────────────────────────────────
    static void mostrarCategoriasRegistradas(String[] nombres,
                                              double[] salarios, int total) {
        if (total == 0) {
            System.out.println("⚠ No hay Coders registrados.");
            return;
        }
        for (int i = 0; i < total; i++) {
            var cat = obtenerCategoriaSalarial(salarios[i]);  // var → String (Java 11+)
            System.out.printf("  %-16s → %s%n", nombres[i], cat);
        }
    }

    // ─────────────────────────────────────────────────────────
    //  TASK 3 – MATRIZ DE DESEMPEÑO + CASTING + TASK 4 TERNARIO
    // ─────────────────────────────────────────────────────────
    /**
     * Captura calificaciones trimestrales, calcula promedios con casting
     * y determina promoción con operador ternario.
     *
     * ESTRUCTURA: double[empleados][trimestres]
     */
    static void procesarDesempeno(Scanner sc, String[] nombres, int total) {

        // Definición de la matriz de desempeño
        double[][] calificaciones = new double[total][TRIMESTRES];

        // ── Captura de datos con validación if/else ────────────
        for (int i = 0; i < total; i++) {
            System.out.println("\n  Calificaciones para: " + nombres[i]);
            for (int t = 0; t < TRIMESTRES; t++) {
                boolean entradaValida = false;
                while (!entradaValida) {
                    try {
                        System.out.printf("    Trimestre %d (%.0f - %.0f): ", t + 1, NOTA_MIN, NOTA_MAX);
                        var nota = sc.nextDouble();      // var → double (Java 11+)
                        sc.nextLine();

                        // TASK 2 – if/else: validación de rango de calificación
                        if (nota < NOTA_MIN || nota > NOTA_MAX) {
                            System.out.println("    ⚠ Nota fuera de rango. Reintente.");
                        } else {
                            calificaciones[i][t] = nota;
                            entradaValida = true;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("    ❌ Ingrese un número válido.");
                        sc.nextLine();
                    }
                }
            }
        }

        // ── Cálculo con bucles for anidados ───────────────────
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              REPORTE DE DESEMPEÑO                   ║");
        System.out.println("╠══════════╦═══════╦═══════╦═══════╦════════╦════════╣");
        System.out.println("║ Coder    ║  T1   ║  T2   ║  T3   ║ Prom.  ║ Score  ║");
        System.out.println("╠══════════╬═══════╬═══════╬═══════╬════════╬════════╣");

        for (int i = 0; i < total; i++) {
            double suma = 0.0;

            // Bucle for anidado para recorrer trimestres
            for (int t = 0; t < TRIMESTRES; t++) {
                suma += calificaciones[i][t];
            }

            double promedio = suma / TRIMESTRES;

            /*
             * TASK 3 – CASTING EXPLÍCITO double → int
             *
             * El promedio es double (p.ej. 87.66...).
             * Al hacer (int) promedio, se trunca la parte decimal:
             *   87.66 → 87   (NO redondea, descarta decimales)
             *
             * PÉRDIDA DE PRECISIÓN documentada:
             *   double promedio     = 87.666...  ← valor real
             *   int    scoreSimpli  = 87          ← truncado (pierde 0.666)
             *
             * Esto es intencional para el "Puntaje Simplificado"
             * del reporte ejecutivo.  Para cálculos financieros o
             * científicos NUNCA se debe perder precisión así sin
             * documentarlo explícitamente.
             */
            int scoreSimplificado = (int) promedio;   // Casting explícito con pérdida de precisión

            /*
             * TASK 4 – OPERADOR TERNARIO para estado de promoción
             *
             * Sintaxis: condicion ? valorSiTrue : valorSiFalse
             *
             * Equivalente if/else:
             *   String estado;
             *   if (promedio >= UMBRAL_PROM) { estado = "✅ PROMOVIDO"; }
             *   else                         { estado = "⛔ EN REVISIÓN"; }
             */
            String estadoPromocion = (promedio >= UMBRAL_PROM) ? "✅ PROMOVIDO" : "⛔ EN REVISIÓN";

            System.out.printf("║ %-8s ║ %5.1f ║ %5.1f ║ %5.1f ║ %6.2f ║ %-6d ║%n",
                    nombres[i].length() > 8 ? nombres[i].substring(0, 8) : nombres[i],
                    calificaciones[i][0],
                    calificaciones[i][1],
                    calificaciones[i][2],
                    promedio,
                    scoreSimplificado);

            System.out.println("║          ║                              Estado: " + estadoPromocion + " ║");
        }

        System.out.println("╚══════════╩═══════╩═══════╩═══════╩════════╩════════╝");
        System.out.println("  * Score Simplificado = (int) promedio  → trunca decimales");
    }
}
