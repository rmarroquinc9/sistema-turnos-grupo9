package com.turnos.dao;

import com.turnos.modelo.Servicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicioDAOImpl implements IServicioDAO {

    private static final String INSERTAR = """
            INSERT INTO servicios (codigo, nombre, estado)
            VALUES (?, ?, ?)
            """;
    private static final String LEER_TODOS = """
            SELECT codigo, nombre, estado
            FROM servicios
            ORDER BY codigo
            """;
    private static final String LEER_ACTIVOS = """
            SELECT codigo, nombre, estado
            FROM servicios
            WHERE estado = 'Activo'
            ORDER BY codigo
            """;
    private static final String BUSCAR_POR_CODIGO = """
            SELECT codigo, nombre, estado
            FROM servicios
            WHERE codigo = ?
            """;
    private static final String ACTUALIZAR = """
            UPDATE servicios
            SET nombre = ?, estado = ?
            WHERE codigo = ?
            """;
    private static final String CAMBIAR_ESTADO = """
            UPDATE servicios
            SET estado = ?
            WHERE codigo = ?
            """;

    @Override
    public void crear(Servicio servicio) throws SQLException {
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(INSERTAR)) {
            statement.setInt(1, servicio.getCodigo());
            statement.setString(2, servicio.getNombre());
            statement.setString(3, servicio.getEstado());
            statement.executeUpdate();
        }
    }

    @Override
    public List<Servicio> leerTodos() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(LEER_TODOS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                servicios.add(mapear(resultSet));
            }
        }
        return servicios;
    }

    @Override
    public List<Servicio> leerActivos() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(LEER_ACTIVOS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                servicios.add(mapear(resultSet));
            }
        }
        return servicios;
    }

    @Override
    public Optional<Servicio> buscarPorCodigo(int codigo) throws SQLException {
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
    public void actualizar(Servicio servicio) throws SQLException {
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(ACTUALIZAR)) {
            statement.setString(1, servicio.getNombre());
            statement.setString(2, servicio.getEstado());
            statement.setInt(3, servicio.getCodigo());
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

    private Servicio mapear(ResultSet resultSet) throws SQLException {
        return new Servicio(
                resultSet.getInt("codigo"),
                resultSet.getString("nombre"),
                resultSet.getString("estado"));
    }
}
