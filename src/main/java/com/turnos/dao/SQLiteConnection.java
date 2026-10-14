package com.turnos.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class SQLiteConnection {

    private static final String URL = "jdbc:sqlite:data/turnos.db";

    private SQLiteConnection() {
    }

    public static Connection obtenerConexion() throws SQLException {
        Connection conexion = DriverManager.getConnection(URL);
        try (Statement statement = conexion.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException exception) {
            cerrarConexion(conexion);
            throw exception;
        }
        return conexion;
    }

    public static void cerrarConexion(Connection conexion) throws SQLException {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }
}
