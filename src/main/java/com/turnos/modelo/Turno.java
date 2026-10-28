package com.turnos.modelo;

import java.time.LocalDateTime;

public class Turno {
    private int numero;
    private int codigoCliente;
    private int codigoServicio;
    private LocalDateTime fechaHoraGeneracion;
    private String estado; // PENDIENTE, ATENDIDO, CANCELADO
    private LocalDateTime fechaHoraAtencionCancelacion;

    public Turno(int numero, int codigoCliente, int codigoServicio, LocalDateTime fechaHoraGeneracion, String estado) {
        this.numero = numero;
        this.codigoCliente = codigoCliente;
        this.codigoServicio = codigoServicio;
        this.fechaHoraGeneracion = fechaHoraGeneracion;
        this.estado = estado;
    }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public int getCodigoCliente() { return codigoCliente; }
    public int getCodigoServicio() { return codigoServicio; }
    public LocalDateTime getFechaHoraGeneracion() { return fechaHoraGeneracion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaHoraAtencionCancelacion() { return fechaHoraAtencionCancelacion; }
    public void setFechaHoraAtencionCancelacion(LocalDateTime fechaHoraAtencionCancelacion) { this.fechaHoraAtencionCancelacion = fechaHoraAtencionCancelacion; }
}