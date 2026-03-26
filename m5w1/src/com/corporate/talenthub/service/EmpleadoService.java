package com.corporate.talenthub.service;

import com.corporate.talenthub.model.Empleado;

public class EmpleadoService {

    public double calcularSalarioFinal(Empleado emp) {

        // Paréntesis -> Multiplicación -> Suma/Resta
        double resultado = (emp.salarioBase + (emp.bonoMensual * 1.10)) - (emp.salarioBase * 0.05);

        emp.bonoMensual += 50;

        if (emp.idEmpleado % 2 == 0) {
            resultado += 100;
        }

        return resultado;
    }

    public boolean validarElegibilidad(Empleado emp, int puntajeTest) {

        // ! -> && -> ||
        return (puntajeTest > 85 && emp.edad < 30) || (emp.idSede == 1 && !emp.esActivo);
    }
}