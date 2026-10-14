package com.turnos;

import com.turnos.vista.MenuPrincipal;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            new MenuPrincipal().iniciar();
        } catch (SQLException exception) {
            System.err.println("No se pudo iniciar la aplicación: " + exception.getMessage());
        }
    }
}