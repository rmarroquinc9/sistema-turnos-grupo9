package com.turnos.dao;

import com.turnos.modelo.ResumenServicio;
import com.turnos.modelo.Turno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TurnoDAOImpl implements ITurnoDAO {

    private static final String INSERTAR = """
            INSERT INTO turnos
                (codigo_cliente, codigo_servicio, fecha_generacion, estado, fecha_atencion)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String LEER_TODOS = """
            SELECT numero, codigo_cliente, codigo_servicio,
                   fecha_generacion, estado, fecha_atencion
            FROM turnos
            ORDER BY numero
            """;
    private static final String BUSCAR_POR_NUMERO = """
            SELECT numero, codigo_cliente, codigo_servicio,
                   fecha_generacion, estado, fecha_atencion
            FROM turnos
            WHERE numero = ?
            """;
    private static final String ACTUALIZAR_ESTADO = """
            UPDATE turnos
            SET estado = ?, fecha_atencion = ?
            WHERE numero = ?
            """;
    private static final String BUSCAR_POR_CLIENTE = """
            SELECT numero, codigo_cliente, codigo_servicio,
                   fecha_generacion, estado, fecha_atencion
            FROM turnos
            WHERE codigo_cliente = ?
            ORDER BY numero
            """;
    private static final String RESUMEN_POR_SERVICIO = """
            SELECT s.nombre,
                   SUM(CASE WHEN t.estado = 'PENDIENTE' THEN 1 ELSE 0 END) AS pendientes,
                   SUM(CASE WHEN t.estado = 'ATENDIDO' THEN 1 ELSE 0 END) AS atendidos,
                   SUM(CASE WHEN t.estado = 'CANCELADO' THEN 1 ELSE 0 END) AS cancelados,
                   COUNT(t.numero) AS total
            FROM servicios s
            LEFT JOIN turnos t ON t.codigo_servicio = s.codigo
            GROUP BY s.codigo, s.nombre
            ORDER BY s.codigo
            """;

    @Override
    public Turno crear(Turno turno) throws SQLException {
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(
                     INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, turno.getCodigoCliente());
            statement.setInt(2, turno.getCodigoServicio());
            statement.setString(3, turno.getFechaGeneracion());
            statement.setString(4, turno.getEstado());
            statement.setString(5, turno.getFechaAtencion());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("No se generó el número del turno");
                }
                return new Turno(
                        generatedKeys.getInt(1),
                        turno.getCodigoCliente(),
                        turno.getCodigoServicio(),
                        turno.getFechaGeneracion(),
                        turno.getEstado(),
                        turno.getFechaAtencion());
            }
        }
    }

    @Override
    public List<Turno> leerTodos() throws SQLException {
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(LEER_TODOS);
             ResultSet resultSet = statement.executeQuery()) {
            return mapearLista(resultSet);
        }
    }

    @Override
    public Optional<Turno> buscarPorNumero(int numero) throws SQLException {
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(BUSCAR_POR_NUMERO)) {
            statement.setInt(1, numero);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapear(resultSet))
                        : Optional.empty();
            }
        }
    }

    @Override
    public void actualizarEstado(int numero, String estado, String fechaAtencion)
            throws SQLException {
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(ACTUALIZAR_ESTADO)) {
            statement.setString(1, estado);
            statement.setString(2, fechaAtencion);
            statement.setInt(3, numero);
            statement.executeUpdate();
        }
    }

    @Override
    public List<Turno> buscarPorCliente(int codigoCliente) throws SQLException {
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(BUSCAR_POR_CLIENTE)) {
            statement.setInt(1, codigoCliente);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapearLista(resultSet);
            }
        }
    }

    @Override
    public List<ResumenServicio> resumenPorServicio() throws SQLException {
        List<ResumenServicio> resumen = new ArrayList<>();
        try (Connection conexion = SQLiteConnection.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(RESUMEN_POR_SERVICIO);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                resumen.add(new ResumenServicio(
                        resultSet.getString("nombre"),
                        resultSet.getLong("pendientes"),
                        resultSet.getLong("atendidos"),
                        resultSet.getLong("cancelados"),
                        resultSet.getLong("total")));
            }
        }
        return resumen;
    }

    private List<Turno> mapearLista(ResultSet resultSet) throws SQLException {
        List<Turno> turnos = new ArrayList<>();
        while (resultSet.next()) {
            turnos.add(mapear(resultSet));
        }
        return turnos;
    }

    private Turno mapear(ResultSet resultSet) throws SQLException {
        return new Turno(
                resultSet.getInt("numero"),
                resultSet.getInt("codigo_cliente"),
                resultSet.getInt("codigo_servicio"),
                resultSet.getString("fecha_generacion"),
                resultSet.getString("estado"),
                resultSet.getString("fecha_atencion"));
    }
}
