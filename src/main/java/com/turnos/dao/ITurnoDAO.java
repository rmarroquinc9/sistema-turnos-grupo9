package com.turnos.dao;

import com.turnos.modelo.Turno;
import java.util.List;
import java.time.LocalDate;
import com.turnos.modelo.ResumenServicio;

public interface ITurnoDAO {
    Turno crear(Turno turno) throws java.sql.SQLException;
    List<Turno> leerTodos() throws java.sql.SQLException;
    void actualizarEstado(int numero, String estado, String fechaAtencion) throws java.sql.SQLException;
    List<Turno> buscarPorCliente(int codigoCliente) throws java.sql.SQLException;
    boolean tieneTurnoPendientePorServicio(int codigoServicio) throws java.sql.SQLException;
    List<ResumenServicio> resumenPorServicio(LocalDate desde, LocalDate hasta,
                                             Integer codigoServicio, String estado)
            throws java.sql.SQLException;

    void insertarTurno(Turno turno);
    void actualizarEstadoTurno(Turno turno);
    List<Turno> obtenerTurnosPendientes();
    int obtenerSiguienteNumeroTurno();
}