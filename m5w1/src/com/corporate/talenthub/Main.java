package com.corporate.talenthub;

import com.corporate.talenthub.model.Empleado;
import com.corporate.talenthub.model.EmpresaRecord;
import com.corporate.talenthub.service.EmpleadoService;

public class Main {

    public static void main(String[] args) {

        String encabezado = """
                === CORPORATE TALENT HUB ===
                Sistema de gestión de talento empresarial
                """;

        System.out.println(encabezado);

        Empleado emp = new Empleado(2, "Juan");
        EmpresaRecord empresa = new EmpresaRecord("TechCorp", "123456-7", 2010);

        EmpleadoService service = new EmpleadoService();

        double salario = service.calcularSalarioFinal(emp);
        boolean elegible = service.validarElegibilidad(emp, 90);

        System.out.println("Salario final: " + salario);
        System.out.println("Elegible: " + elegible);
        System.out.println("Empresa: " + empresa.nombre());

        emp.nombre = null;

        Empleado emp2 = new Empleado(2, "Juan");
        System.out.println(emp == emp2);
    }
}