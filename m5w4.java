// =============================================================
//  CORPORATE TALENT HUB — Arquitectura POO Avanzada
//  Comparativa Legacy (Java 8/11) vs Moderno (Java 17/21)
// =============================================================

// ─────────────────────────────────────────────────────────────
// TASK 4 · Interfaz Promocionable
// ─────────────────────────────────────────────────────────────

/**
 * Contrato de comportamiento para empleados promocionables.
 *
 * MODERNO (Java 8+): el método default 'registrarLog' se añadió
 * sin romper las clases que ya implementaban la interfaz.
 * Antes de Java 8 esto era imposible; había que modificar cada
 * clase implementadora manualmente.
 */
interface Promocionable {

    // Método abstracto: cada rol calcula su bono de forma distinta
    double calcularBonoAscenso();

    // Método default: lógica compartida sin afectar implementaciones existentes
    default void registrarLog(String nombreEmpleado) {
        System.out.printf("[LOG] Bono calculado para %s: $%.2f%n",
                nombreEmpleado, calcularBonoAscenso());
    }
}


// ─────────────────────────────────────────────────────────────
// TASK 1 · Jerarquía sellada (Sealed Class)
// ─────────────────────────────────────────────────────────────

/*
 * LEGACY (Java 8/11):
 *
 *   public abstract class Persona { ... }
 *
 * Cualquier clase del proyecto —o de una librería externa— puede
 * extender Persona sin restricción. El compilador no puede
 * garantizar qué subtipos existen, lo que dificulta el
 * mantenimiento y el análisis exhaustivo en switch/pattern matching.
 *
 * ──────────────────────────────────────────────────────────────
 * MODERNO (Java 17/21) — Sealed Class:
 * La palabra clave `sealed … permits` lista explícitamente las
 * únicas clases que pueden heredar. Ventajas:
 *   1. El dominio queda protegido: nadie puede añadir subtipos
 *      inesperados desde fuera del paquete.
 *   2. El compilador verifica exhaustividad en switch expressions,
 *      eliminando la necesidad de un caso `default` defensivo.
 *   3. Mejora la legibilidad: con solo leer `permits` sabes todo
 *      el árbol de herencia posible.
 */
sealed abstract class Persona permits Empleado, ConsultorExterno {

    // private → solo accesible dentro de Persona
    private final int    id;
    private final String nombre;

    protected Persona(int id, String nombre) {
        this.id     = id;
        this.nombre = nombre;
    }

    // Getters expuestos a subclases y consumidores
    public int    getId()     { return id; }
    public String getNombre() { return nombre; }

    public abstract void mostrarPerfil();
}


// ─────────────────────────────────────────────────────────────
// TASK 1 · Subclases permitidas de Persona
// ─────────────────────────────────────────────────────────────

/**
 * Empleado es `non-sealed`: puede ser extendido libremente
 * (por Desarrollador y Gerente), pero solo porque Persona lo autoriza.
 */
non-sealed abstract class Empleado extends Persona implements Promocionable {

    // protected → visible en subclases directas
    protected final String departamento;

    protected Empleado(int id, String nombre, String departamento) {
        super(id, nombre);
        this.departamento = departamento;
    }

    public String getDepartamento() { return departamento; }
}

/**
 * ConsultorExterno es `final`: no puede ser extendido.
 * Rama sellada terminada.
 */
final class ConsultorExterno extends Persona {

    private final String empresa;

    public ConsultorExterno(int id, String nombre, String empresa) {
        super(id, nombre);
        this.empresa = empresa;
    }

    public String getEmpresa() { return empresa; }

    @Override
    public void mostrarPerfil() {
        System.out.printf("Consultor Externo | %s | Empresa: %s%n",
                getNombre(), empresa);
    }
}


// ─────────────────────────────────────────────────────────────
// TASK 3 · Subclases concretas de Empleado
// ─────────────────────────────────────────────────────────────

final class Desarrollador extends Empleado {

    private final String lenguajePrincipal;

    public Desarrollador(int id, String nombre, String lenguajePrincipal) {
        super(id, nombre, "Ingeniería");
        this.lenguajePrincipal = lenguajePrincipal;
    }

    public String getLenguajePrincipal() { return lenguajePrincipal; }

    @Override
    public double calcularBonoAscenso() {
        // Bono fijo por dominar un lenguaje técnico
        return 1_500.00;
    }

    @Override
    public void mostrarPerfil() {
        System.out.printf("Desarrollador | %s | Lenguaje: %s%n",
                getNombre(), lenguajePrincipal);
    }
}

final class Gerente extends Empleado {

    private final double presupuestoMensual;

    public Gerente(int id, String nombre, double presupuestoMensual) {
        super(id, nombre, "Dirección");
        this.presupuestoMensual = presupuestoMensual;
    }

    public double getPresupuestoMensual() { return presupuestoMensual; }

    @Override
    public double calcularBonoAscenso() {
        // Bono: 10 % del presupuesto mensual gestionado
        return presupuestoMensual * 0.10;
    }

    @Override
    public void mostrarPerfil() {
        System.out.printf("Gerente | %s | Presupuesto: $%.2f%n",
                getNombre(), presupuestoMensual);
    }
}


// ─────────────────────────────────────────────────────────────
// TASK 2 · Record inmutable — DesempeñoReport
// ─────────────────────────────────────────────────────────────

/*
 * LEGACY (Java 8/11) — POJO tradicional requería:
 *   - Constructor con todos los campos
 *   - Getters manuales (getId, getPromedio, getFeedback)
 *   - toString() manual
 *   - equals() y hashCode() manuales
 *   ~30 líneas de código boilerplate para 3 campos.
 *
 * ──────────────────────────────────────────────────────────────
 * MODERNO (Java 17/21) — Record:
 * Una sola línea genera automáticamente:
 *   ✔ Constructor canónico
 *   ✔ Getters (idEmpleado(), promedio(), feedback())
 *   ✔ equals(), hashCode() y toString()
 *   ✔ Inmutabilidad garantizada (todos los campos son final)
 *
 * Ideal para objetos de valor como reportes, DTOs y respuestas.
 */
record DesempeñoReport(int idEmpleado, double promedio, String feedback) {}


// ─────────────────────────────────────────────────────────────
// TASK 3 · Servicio de validación — Pattern Matching
// ─────────────────────────────────────────────────────────────

class EmpleadoService {

    /**
     * LEGACY (Java 8/11): instanceof + casting manual.
     * Problema: si el cast es incorrecto en runtime → ClassCastException.
     * Además, se repite la referencia a la variable original (p).
     */
    static void validarLegacy(Empleado p) {
        if (p instanceof Desarrollador) {
            // Cast manual obligatorio para acceder al método específico
            String lang = ((Desarrollador) p).getLenguajePrincipal();
            System.out.println("[Legacy] Lenguaje: " + lang);

        } else if (p instanceof Gerente) {
            double budget = ((Gerente) p).getPresupuestoMensual();
            System.out.println("[Legacy] Presupuesto: $" + budget);
        }
    }

    /**
     * MODERNO (Java 17/21): Pattern Matching for instanceof.
     *
     * Ventajas:
     *   1. No hay casting manual → imposible ClassCastException.
     *   2. La variable `dev` / `ger` ya es del tipo correcto en su bloque.
     *   3. Código más corto, más expresivo y más seguro.
     */
    static void validarModerno(Empleado p) {
        if (p instanceof Desarrollador dev) {
            System.out.println("[Moderno] Lenguaje: " + dev.getLenguajePrincipal());

        } else if (p instanceof Gerente ger) {
            System.out.printf("[Moderno] Presupuesto: $%.2f%n", ger.getPresupuestoMensual());
        }
    }

    /** Emite un DesempeñoReport inmutable de fin de mes para un empleado. */
    static DesempeñoReport generarReporte(Empleado emp, double promedio) {
        String feedback = promedio >= 8.0 ? "Excelente desempeño" : "Requiere mejora";
        return new DesempeñoReport(emp.getId(), promedio, feedback);
    }
}


// ─────────────────────────────────────────────────────────────
// Main — demostración integrada
// ─────────────────────────────────────────────────────────────

public class CorporateTalentHub {

    public static void main(String[] args) {

        // Crear empleados
        Desarrollador dev = new Desarrollador(1, "Ana Torres", "Java");
        Gerente       ger = new Gerente(2, "Luis Pérez", 20_000.00);
        ConsultorExterno con = new ConsultorExterno(3, "Sara Gil", "TechCorp");

        System.out.println("=== Perfiles ===");
        dev.mostrarPerfil();
        ger.mostrarPerfil();
        con.mostrarPerfil();

        // Task 3: validación Legacy vs Moderno
        System.out.println("\n=== Validación ===");
        EmpleadoService.validarLegacy(dev);
        EmpleadoService.validarModerno(dev);
        EmpleadoService.validarLegacy(ger);
        EmpleadoService.validarModerno(ger);

        // Task 4: polimorfismo + método default
        System.out.println("\n=== Bonos de Ascenso ===");
        dev.registrarLog(dev.getNombre());
        ger.registrarLog(ger.getNombre());

        // Task 2: Records inmutables de fin de mes
        System.out.println("\n=== Reportes de Desempeño ===");
        DesempeñoReport repDev = EmpleadoService.generarReporte(dev, 9.1);
        DesempeñoReport repGer = EmpleadoService.generarReporte(ger, 7.4);
        System.out.println(repDev);
        System.out.println(repGer);
    }
}
