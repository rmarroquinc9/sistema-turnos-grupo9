package com.turnos.modelo;

public class Turno {
    private int numero;
    private int codigoCliente;
    private int codigoServicio;
    private String fechaGeneracion;
    private String estado; // PENDIENTE, ATENDIDO, CANCELADO
    private String fechaAtencion;

    public Turno(int numero, int codigoCliente, int codigoServicio, String fechaGeneracion,
                 String estado, String fechaAtencion) {
        this.numero = numero;
        this.codigoCliente = codigoCliente;
        this.codigoServicio = codigoServicio;
        this.fechaGeneracion = fechaGeneracion;
        this.estado = estado;
        this.fechaAtencion = fechaAtencion;
    }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public int getCodigoCliente() { return codigoCliente; }
    public int getCodigoServicio() { return codigoServicio; }
    public String getFechaGeneracion() { return fechaGeneracion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getFechaAtencion() { return fechaAtencion; }
    public void setFechaAtencion(String fechaAtencion) { this.fechaAtencion = fechaAtencion; }
}