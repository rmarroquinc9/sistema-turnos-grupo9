package com.turnos.estructuras;

import com.turnos.modelo.Turno;
import java.util.ArrayList;
import java.util.List;

public class ColaEnlazada<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;

    public ColaEnlazada() {
        this.frente = null;
        this.fin = null;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public void encolar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (estaVacia()) {
            frente = nuevoNodo;
        } else {
            fin.setSiguiente(nuevoNodo);
        }
        fin = nuevoNodo;
    }

    public T desencolar() {
        if (estaVacia()) {
            return null;
        }
        T elemento = frente.getElemento();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        return elemento;
    }

    public T consultarPrimero() {
        if (estaVacia()) return null;
        return frente.getElemento();
    }

    public boolean cancelarPorNumero(int numero) {
        if (estaVacia()) return false;

        if (esTurnoConNumero(frente.getElemento(), numero)) {
            frente = frente.getSiguiente();
            if (frente == null) fin = null;
            return true;
        }

        Nodo<T> actual = frente;
        while (actual.getSiguiente() != null
                && !esTurnoConNumero(actual.getSiguiente().getElemento(), numero)) {
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

    public List<T> recorrer() {
        List<T> elementos = new ArrayList<>();
        Nodo<T> actual = frente;
        while (actual != null) {
            elementos.add(actual.getElemento());
            actual = actual.getSiguiente();
        }
        return elementos;
    }

    public T consultarPrimerTurno() {
        return consultarPrimero();
    }

    public boolean cancelarTurnoPorNumero(int numero) {
        return cancelarPorNumero(numero);
    }

    public void recorrerYMostrar() {
        if (estaVacia()) {
            System.out.println("No hay turnos pendientes en la cola.");
            return;
        }
        Nodo<T> actual = frente;
        while (actual != null) {
            if (!(actual.getElemento() instanceof Turno t)) {
                actual = actual.getSiguiente();
                continue;
            }
            System.out.println("Turno: " + t.getNumero() + " | Cliente: " + t.getCodigoCliente() + " | Servicio: " + t.getCodigoServicio());
            actual = actual.getSiguiente();
        }
    }

    private boolean esTurnoConNumero(T elemento, int numero) {
        return elemento instanceof Turno turno && turno.getNumero() == numero;
    }
}