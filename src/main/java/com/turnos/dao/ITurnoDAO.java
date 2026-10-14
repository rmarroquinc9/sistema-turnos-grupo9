package com.turnos.dao;

import com.turnos.modelo.Turno;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ITurnoDAO {

    Turno crear(Turno turno) throws SQLException;

    List<Turno> leerTodos() throws SQLException;

    Optional<Turno> buscarPorNumero(int numero) throws SQLException;

    void actualizarEstado(int numero, String estado, String fechaAtencion) throws SQLException;

    List<Turno> buscarPorCliente(int codigoCliente) throws SQLException;

    Map<String, Long> resumenPorServicio() throws SQLException;
}
