package com.turnos.dao;

import com.turnos.modelo.Turno;
import java.util.List;

public interface ITurnoDAO {
    void insertarTurno(Turno turno);
    void actualizarEstadoTurno(Turno turno);
    List<Turno> obtenerTurnosPendientes();
    int obtenerSiguienteNumeroTurno();
}