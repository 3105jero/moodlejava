package com.corporate.talenthub.model;

public class Empleado {

    public byte edad = 25;
    public short idSede = 1;
    public int idEmpleado;
    public long identificacion = 123456789L;
    public float bonoMensual = 200.5f;
    public double salarioBase = 2000.75;
    public char genero = 'M';
    public boolean esActivo = true;

    public String nombre;

    public Empleado(int idEmpleado, String nombre) {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
    }
}