package com.turnos.modelo;

import java.util.Objects;

public class Cliente {

    private final int codigo;
    private String nombre;
    private String telefono;
    private String estado;

    public Cliente(int codigo, String nombre, String telefono, String estado) {
        this.codigo = codigo;
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.telefono = validarTelefono(telefono);
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = validarTelefono(telefono);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
    }

    private static String validarTelefono(String telefono) {
        if (telefono == null || !telefono.matches("\\d{8}")) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 8 dígitos");
        }
        return telefono;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "codigo=" + codigo +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
