package com.turnos.modelo;

import java.util.Objects;

public class Servicio {

    private final int codigo;
    private String nombre;
    private String estado;

    public Servicio(int codigo, String nombre, String estado) {
        this.codigo = codigo;
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
    }

    @Override
    public String toString() {
        return "Servicio{" +
                "codigo=" + codigo +
                ", nombre='" + nombre + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
