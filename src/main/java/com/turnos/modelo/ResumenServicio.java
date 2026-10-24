package com.turnos.modelo;

public record ResumenServicio(
        int codigoServicio,
        String nombreServicio,
        long pendientes,
        long atendidos,
        long cancelados,
        long total,
        double promedioMinutosAtencion) {

    public double porcentajeCancelacion() {
        return total == 0 ? 0 : cancelados * 100.0 / total;
    }
}
