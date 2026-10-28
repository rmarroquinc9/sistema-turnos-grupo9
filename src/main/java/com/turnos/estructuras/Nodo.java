package com.turnos.estructuras;

import com.turnos.modelo.Turno;

public class Nodo {
    private Turno turno;
    private Nodo siguiente;

    public Nodo(Turno turno) {
        this.turno = turno;
        this.siguiente = null;
    }

    public Turno getTurno() { return turno; }
    public Nodo getSiguiente() { return siguiente; }
    public void setSiguiente(Nodo siguiente) { this.siguiente = siguiente; }
}