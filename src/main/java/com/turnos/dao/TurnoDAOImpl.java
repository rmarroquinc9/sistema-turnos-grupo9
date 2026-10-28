package com.turnos.dao;

import com.turnos.modelo.Turno;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAOImpl implements ITurnoDAO {

    @Override
    public void insertarTurno(Turno turno) {
        String sql = "INSERT INTO turnos(numero, codigo_cliente, codigo_servicio, fecha_generacion, estado) VALUES(?,?,?,?,?)";
        // Asume que SQLiteConnection.getConnection() retorna un java.sql.Connection
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, turno.getNumero());
            pstmt.setInt(2, turno.getCodigoCliente());
            pstmt.setInt(3, turno.getCodigoServicio());
            pstmt.setString(4, turno.getFechaHoraGeneracion().toString());
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
            pstmt.setString(2, turno.getFechaHoraAtencionCancelacion() != null ? turno.getFechaHoraAtencionCancelacion().toString() : null);
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
                Turno t = new Turno(
                    rs.getInt("numero"),
                    rs.getInt("codigo_cliente"),
                    rs.getInt("codigo_servicio"),
                    LocalDateTime.parse(rs.getString("fecha_generacion")),
                    rs.getString("estado")
                );
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
}