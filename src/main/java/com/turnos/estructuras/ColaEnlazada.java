package com.turnos.estructuras;

import com.turnos.modelo.Turno;

public class ColaEnlazada {
    private Nodo frente;
    private Nodo fin;

    public ColaEnlazada() {
        this.frente = null;
        this.fin = null;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public void encolar(Turno turno) {
        Nodo nuevoNodo = new Nodo(turno);
        if (estaVacia()) {
            frente = nuevoNodo;
        } else {
            fin.setSiguiente(nuevoNodo);
        }
        fin = nuevoNodo;
    }

    public Turno desencolar() {
        if (estaVacia()) {
            return null;
        }
        Turno turno = frente.getTurno();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        return turno;
    }

    public Turno consultarPrimerTurno() {
        if (estaVacia()) return null;
        return frente.getTurno();
    }

    public boolean cancelarTurnoPorNumero(int numero) {
        if (estaVacia()) return false;

        // Si el turno a cancelar está al frente
        if (frente.getTurno().getNumero() == numero) {
            frente = frente.getSiguiente();
            if (frente == null) fin = null;
            return true;
        }

        // Si está en medio o al final
        Nodo actual = frente;
        while (actual.getSiguiente() != null && actual.getSiguiente().getTurno().getNumero() != numero) {
            actual = actual.getSiguiente();
        }

        if (actual.getSiguiente() != null) {
            if (actual.getSiguiente() == fin) {
                fin = actual; // Actualizar el fin si se elimina el último
            }
            actual.setSiguiente(actual.getSiguiente().getSiguiente());
            return true;
        }

        return false;
    }

    public void recorrerYMostrar() {
        if (estaVacia()) {
            System.out.println("No hay turnos pendientes en la cola.");
            return;
        }
        Nodo actual = frente;
        while (actual != null) {
            Turno t = actual.getTurno();
            System.out.println("Turno: " + t.getNumero() + " | Cliente: " + t.getCodigoCliente() + " | Servicio: " + t.getCodigoServicio());
            actual = actual.getSiguiente();
        }
    }
}