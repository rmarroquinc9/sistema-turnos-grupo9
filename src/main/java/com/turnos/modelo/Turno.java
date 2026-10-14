package com.turnos.modelo;

import java.util.Objects;

public class Turno {

    private final int numero;
    private int codigoCliente;
    private int codigoServicio;
    private String fechaGeneracion;
    private String estado;
    private String fechaAtencion;

    public Turno(int numero, int codigoCliente, int codigoServicio,
                 String fechaGeneracion, String estado, String fechaAtencion) {
        this.numero = numero;
        this.codigoCliente = codigoCliente;
        this.codigoServicio = codigoServicio;
        this.fechaGeneracion = Objects.requireNonNull(
                fechaGeneracion, "La fecha de generación no puede ser nula");
        this.estado = validarEstado(estado);
        this.fechaAtencion = fechaAtencion;
    }

    public int getNumero() {
        return numero;
    }

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public int getCodigoServicio() {
        return codigoServicio;
    }

    public void setCodigoServicio(int codigoServicio) {
        this.codigoServicio = codigoServicio;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = Objects.requireNonNull(
                fechaGeneracion, "La fecha de generación no puede ser nula");
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = validarEstado(estado);
    }

    public String getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(String fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    private static String validarEstado(String estado) {
        if (!"PENDIENTE".equals(estado)
                && !"ATENDIDO".equals(estado)
                && !"CANCELADO".equals(estado)) {
            throw new IllegalArgumentException(
                    "El estado debe ser PENDIENTE, ATENDIDO o CANCELADO");
        }
        return estado;
    }

    @Override
    public String toString() {
        return "Turno{" +
                "numero=" + numero +
                ", codigoCliente=" + codigoCliente +
                ", codigoServicio=" + codigoServicio +
                ", fechaGeneracion='" + fechaGeneracion + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaAtencion='" + fechaAtencion + '\'' +
                '}';
    }
}
