package com.turnos.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class SQLiteConnection {

    private static final String URL = "jdbc:sqlite:data/turnos.db";
    private static volatile boolean indicesInicializados;

    private SQLiteConnection() {
    }

    public static Connection obtenerConexion() throws SQLException {
        Connection conexion = DriverManager.getConnection(URL);
        try (Statement statement = conexion.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            asegurarIndices(statement);
        } catch (SQLException exception) {
            cerrarConexion(conexion);
            throw exception;
        }

        return conexion;
    }

    public static Connection getConnection() throws SQLException {
        return obtenerConexion();
    }

    private static void asegurarIndices(Statement statement) throws SQLException {
        if (indicesInicializados) {
            return;
        }
        synchronized (SQLiteConnection.class) {
            if (indicesInicializados) {
                return;
            }
            statement.execute(
                    "CREATE INDEX IF NOT EXISTS idx_turnos_codigo_cliente "
                            + "ON turnos(codigo_cliente)");
            statement.execute(
                    "CREATE INDEX IF NOT EXISTS idx_turnos_codigo_servicio "
                            + "ON turnos(codigo_servicio)");
            statement.execute(
                    "CREATE INDEX IF NOT EXISTS idx_turnos_estado "
                            + "ON turnos(estado)");
            statement.execute(
                    "CREATE INDEX IF NOT EXISTS idx_turnos_fecha_generacion "
                            + "ON turnos(fecha_generacion)");
            indicesInicializados = true;
        }
    }

    public static void cerrarConexion(Connection conexion) throws SQLException {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }
}
