package com.turnos.dao;

import com.turnos.modelo.Servicio;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IServicioDAO {

    void crear(Servicio servicio) throws SQLException;

    List<Servicio> leerTodos() throws SQLException;

    Optional<Servicio> buscarPorCodigo(int codigo) throws SQLException;

    void actualizar(Servicio servicio) throws SQLException;

    void cambiarEstado(int codigo, String estado) throws SQLException;
}
