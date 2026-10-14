package com.turnos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Inicializador {

    private static final Path SCRIPT_SQL = Path.of("sql", "crear_y_cargar.sql");
    private static final Path DIRECTORIO_BD = Path.of("data");

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            System.out.println("Error: No se encontró el driver de SQLite. "
                    + "Verifica que sqlite-jdbc esté en el classpath.");
            return;
        }

        try (BufferedReader lector = new BufferedReader(
                new FileReader(SCRIPT_SQL.toFile()))) {
            Files.createDirectories(DIRECTORIO_BD);
            String rutaAbsoluta = DIRECTORIO_BD.resolve("turnos.db")
                    .toAbsolutePath()
                    .toString();
            String URL_BD = "jdbc:sqlite:" + rutaAbsoluta;
            System.out.println("Ruta de la base de datos: " + rutaAbsoluta);

            StringBuilder contenido = new StringBuilder();
            String linea;
            while ((linea = lector.readLine()) != null) {
                contenido.append(linea).append(System.lineSeparator());
            }
            ejecutarScript(contenido.toString(), URL_BD);
            System.out.println("Base de datos creada exitosamente");
        } catch (IOException exception) {
            System.err.println("Error de entrada/salida: " + exception.getMessage());
        } catch (SQLException exception) {
            System.err.println("Error de base de datos: " + exception.getMessage());
        }
    }

    private static void ejecutarScript(String script, String urlBd) throws SQLException {
        try (Connection conexion = DriverManager.getConnection(urlBd);
             Statement statement = conexion.createStatement()) {
            for (String instruccion : script.split(";")) {
                String sql = instruccion.trim();
                if (!sql.isEmpty() && contieneInstruccion(sql)) {
                    statement.execute(sql);
                }
            }
        }
    }

    private static boolean contieneInstruccion(String sql) {
        for (String linea : sql.split("\\R")) {
            if (!linea.trim().isEmpty() && !linea.trim().startsWith("--")) {
                return true;
            }
        }
        return false;
    }
}