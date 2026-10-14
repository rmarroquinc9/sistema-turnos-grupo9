package com.turnos.estructuras;

import com.turnos.modelo.Turno;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ColaEnlazada<T> {

    private Nodo<T> frente;
    private Nodo<T> finalCola;

    public void encolar(T dato) {
        Objects.requireNonNull(dato, "El dato no puede ser nulo");

        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (estaVacia()) {
            frente = nuevoNodo;
            finalCola = nuevoNodo;
            return;
        }

        finalCola.setSiguiente(nuevoNodo);
        finalCola = nuevoNodo;
    }

    public T desencolar() {
        if (estaVacia()) {
            return null;
        }

        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            finalCola = null;
        }
        return dato;
    }

    public T consultarPrimero() {
        return estaVacia() ? null : frente.getDato();
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public List<T> recorrer() {
        List<T> elementos = new ArrayList<>();
        Nodo<T> actual = frente;

        while (actual != null) {
            elementos.add(actual.getDato());
            actual = actual.getSiguiente();
        }
        return elementos;
    }

    public boolean cancelarPorNumero(int numero) {
        Nodo<T> anterior = null;
        Nodo<T> actual = frente;

        while (actual != null) {
            T dato = actual.getDato();
            if (dato instanceof Turno turno && turno.getNumero() == numero) {
                if (anterior == null) {
                    frente = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }

                if (actual == finalCola) {
                    finalCola = anterior;
                }
                return true;
            }

            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }
}
