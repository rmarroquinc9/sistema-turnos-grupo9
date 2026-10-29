package com.turnos.dao;

import com.turnos.modelo.Turno;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.turnos.modelo.ResumenServicio;

public class TurnoDAOImpl implements ITurnoDAO {

    @Override
    public Turno crear(Turno turno) throws SQLException {
        String sql = "INSERT INTO turnos(codigo_cliente, codigo_servicio, fecha_generacion, estado, fecha_atencion) "
                + "VALUES(?,?,?,?,?)";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, turno.getCodigoCliente());
            statement.setInt(2, turno.getCodigoServicio());
            statement.setString(3, turno.getFechaGeneracion());
            statement.setString(4, turno.getEstado());
            statement.setString(5, turno.getFechaAtencion());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("No se pudo obtener el número del turno creado");
                }
                turno.setNumero(keys.getInt(1));
            }
        }
        return turno;
    }

    @Override
    public List<Turno> leerTodos() throws SQLException {
        List<Turno> turnos = new ArrayList<>();
        String sql = "SELECT numero, codigo_cliente, codigo_servicio, fecha_generacion, estado, fecha_atencion "
                + "FROM turnos ORDER BY numero";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                turnos.add(mapear(rs));
            }
        }
        return turnos;
    }

    @Override
    public void actualizarEstado(int numero, String estado, String fechaAtencion) throws SQLException {
        String sql = "UPDATE turnos SET estado = ?, fecha_atencion = ? WHERE numero = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, estado);
            statement.setString(2, fechaAtencion);
            statement.setInt(3, numero);
            statement.executeUpdate();
        }
    }

    @Override
    public List<Turno> buscarPorCliente(int codigoCliente) throws SQLException {
        List<Turno> turnos = new ArrayList<>();
        String sql = "SELECT numero, codigo_cliente, codigo_servicio, fecha_generacion, estado, fecha_atencion "
                + "FROM turnos WHERE codigo_cliente = ? ORDER BY numero";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, codigoCliente);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    turnos.add(mapear(rs));
                }
            }
        }
        return turnos;
    }

    @Override
    public boolean tieneTurnoPendientePorServicio(int codigoServicio) throws SQLException {
        String sql = "SELECT 1 FROM turnos WHERE codigo_servicio = ? AND estado = 'PENDIENTE' LIMIT 1";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, codigoServicio);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<ResumenServicio> resumenPorServicio(LocalDate desde, LocalDate hasta,
                                                     Integer codigoServicio, String estado)
            throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT s.codigo, s.nombre, "
                        + "SUM(CASE WHEN t.estado = 'PENDIENTE' THEN 1 ELSE 0 END) pendientes, "
                        + "SUM(CASE WHEN t.estado = 'ATENDIDO' THEN 1 ELSE 0 END) atendidos, "
                        + "SUM(CASE WHEN t.estado = 'CANCELADO' THEN 1 ELSE 0 END) cancelados, "
                        + "COUNT(t.numero) total, "
                        + "COALESCE(AVG(CASE WHEN t.estado = 'ATENDIDO' AND t.fecha_atencion IS NOT NULL "
                        + "THEN (julianday(t.fecha_atencion) - julianday(t.fecha_generacion)) * 1440 END), 0) promedio "
                        + "FROM servicios s LEFT JOIN turnos t ON s.codigo = t.codigo_servicio");
        List<Object> parametros = new ArrayList<>();
        boolean hasWhere = false;
        if (desde != null) {
            sql.append(" WHERE t.fecha_generacion >= ?");
            parametros.add(desde + "T00:00:00");
            hasWhere = true;
        }
        if (hasta != null) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" t.fecha_generacion < ?");
            parametros.add(hasta.plusDays(1) + "T00:00:00");
            hasWhere = true;
        }
        if (codigoServicio != null) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" s.codigo = ?");
            parametros.add(codigoServicio);
            hasWhere = true;
        }
        if (estado != null) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" t.estado = ?");
            parametros.add(estado);
        }
        sql.append(" GROUP BY s.codigo, s.nombre ORDER BY s.codigo");

        List<ResumenServicio> resumenes = new ArrayList<>();
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                statement.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    resumenes.add(new ResumenServicio(
                            rs.getInt("codigo"), rs.getString("nombre"),
                            rs.getLong("pendientes"), rs.getLong("atendidos"),
                            rs.getLong("cancelados"), rs.getLong("total"),
                            rs.getDouble("promedio")));
                }
            }
        }
        return resumenes;
    }

    @Override
    public void insertarTurno(Turno turno) {
        String sql = "INSERT INTO turnos(numero, codigo_cliente, codigo_servicio, fecha_generacion, estado) VALUES(?,?,?,?,?)";
        // Asume que SQLiteConnection.getConnection() retorna un java.sql.Connection
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, turno.getNumero());
            pstmt.setInt(2, turno.getCodigoCliente());
            pstmt.setInt(3, turno.getCodigoServicio());
            pstmt.setString(4, turno.getFechaGeneracion());
            pstmt.setString(5, turno.getEstado());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al insertar turno: " + e.getMessage());
        }
    }

    @Override
    public void actualizarEstadoTurno(Turno turno) {
        String sql = "UPDATE turnos SET estado = ?, fecha_atencion_cancelacion = ? WHERE numero = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, turno.getEstado());
            pstmt.setString(2, turno.getFechaAtencion());
            pstmt.setInt(3, turno.getNumero());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar turno: " + e.getMessage());
        }
    }

    @Override
    public List<Turno> obtenerTurnosPendientes() {
        List<Turno> pendientes = new ArrayList<>();
        String sql = "SELECT * FROM turnos WHERE estado = 'PENDIENTE' ORDER BY numero ASC";
        
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Turno t = mapear(rs);
                pendientes.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener turnos pendientes: " + e.getMessage());
        }
        return pendientes;
    }

    @Override
    public int obtenerSiguienteNumeroTurno() {
        String sql = "SELECT MAX(numero) AS max_num FROM turnos";
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("max_num") + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error al generar número de turno: " + e.getMessage());
        }
        return 1; // Si no hay registros, inicia en 1
    }

    private Turno mapear(ResultSet rs) throws SQLException {
        return new Turno(
                rs.getInt("numero"),
                rs.getInt("codigo_cliente"),
                rs.getInt("codigo_servicio"),
                rs.getString("fecha_generacion"),
                rs.getString("estado"),
                rs.getString("fecha_atencion"));
    }
}