package com.turnos.modelo;

public record ResumenServicio(
        String nombreServicio,
        long pendientes,
        long atendidos,
        long cancelados,
        long total) {
}
