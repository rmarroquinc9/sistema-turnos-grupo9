package com.turnos.dao;

import com.turnos.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAOImpl implements IClienteDAO {

    private static final String INSERTAR = """
            INSERT INTO clientes (codigo, nombre, telefono, estado)
            VALUES (?, ?, ?, ?)
            """;
    private static final String LEER_TODOS = """
            SELECT codigo, nombre, telefono, estado
            FROM clientes
            ORDER BY codigo
            """;
    private static final String BUSCAR_POR_CODIGO = """
            SELECT codigo, nombre, telefono, estado
            FROM clientes
            WHERE codigo = ?
            """;
    private static final String ACTUALIZAR = """
            UPDATE clientes
            SET nombre = ?, telefono = ?, estado = ?
            WHERE codigo = ?
            """;
    private static final String CAMBIAR_ESTADO = """
            UPDATE clientes
            SET estado = ?
            WHERE codigo = ?
            """;

    @Override
    public void crear(Cliente cliente) throws SQLException {
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(INSERTAR)) {
            statement.setInt(1, cliente.getCodigo());
            statement.setString(2, cliente.getNombre());
            statement.setString(3, cliente.getTelefono());
            statement.setString(4, cliente.getEstado());
            statement.executeUpdate();
        }
    }

    @Override
    public List<Cliente> leerTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(LEER_TODOS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                clientes.add(mapear(resultSet));
            }
        }
        return clientes;
    }

    @Override
    public Optional<Cliente> buscarPorCodigo(int codigo) throws SQLException {
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(BUSCAR_POR_CODIGO)) {
            statement.setInt(1, codigo);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapear(resultSet))
                        : Optional.empty();
            }
        }
    }

    @Override
    public void actualizar(Cliente cliente) throws SQLException {
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(ACTUALIZAR)) {
            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getTelefono());
            statement.setString(3, cliente.getEstado());
            statement.setInt(4, cliente.getCodigo());
            statement.executeUpdate();
        }
    }

    @Override
    public void cambiarEstado(int codigo, String estado) throws SQLException {
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(CAMBIAR_ESTADO)) {
            statement.setString(1, estado);
            statement.setInt(2, codigo);
            statement.executeUpdate();
        }
    }

    private Cliente mapear(ResultSet resultSet) throws SQLException {
        return new Cliente(
                resultSet.getInt("codigo"),
                resultSet.getString("nombre"),
                resultSet.getString("telefono"),
                resultSet.getString("estado"));
    }
}
