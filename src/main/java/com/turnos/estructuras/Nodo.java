package com.turnos.estructuras;

public class Nodo<T> {
    private T elemento;
    private Nodo<T> siguiente;

    public Nodo(T elemento) {
        this.elemento = elemento;
        this.siguiente = null;
    }

    public T getElemento() { return elemento; }
    public Nodo<T> getSiguiente() { return siguiente; }
    public void setSiguiente(Nodo<T> siguiente) { this.siguiente = siguiente; }
}