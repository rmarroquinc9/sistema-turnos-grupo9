package com.turnos.dao;

import com.turnos.modelo.Cliente;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IClienteDAO {

    void crear(Cliente cliente) throws SQLException;

    List<Cliente> leerTodos() throws SQLException;

    Optional<Cliente> buscarPorCodigo(int codigo) throws SQLException;

    void actualizar(Cliente cliente) throws SQLException;

    void cambiarEstado(int codigo, String estado) throws SQLException;
}
