package com.turnos.estructuras;

import com.turnos.modelo.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BST {

    private NodoBST raiz;

    public void insertar(Cliente cliente) {
        Objects.requireNonNull(cliente, "El cliente no puede ser nulo");
        raiz = insertarRecursivo(raiz, cliente);
    }

    public Cliente buscar(int codigo) {
        return buscarRecursivo(raiz, codigo);
    }

    public List<Cliente> recorridoInorden() {
        List<Cliente> clientes = new ArrayList<>();
        recorridoInordenRecursivo(raiz, clientes);
        return clientes;
    }

    private NodoBST insertarRecursivo(NodoBST nodo, Cliente cliente) {
        if (nodo == null) {
            return new NodoBST(cliente);
        }

        if (cliente.getCodigo() < nodo.getDato().getCodigo()) {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), cliente));
        } else if (cliente.getCodigo() > nodo.getDato().getCodigo()) {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), cliente));
        }
        return nodo;
    }

    private Cliente buscarRecursivo(NodoBST nodo, int codigo) {
        if (nodo == null) {
            return null;
        }
        if (codigo == nodo.getDato().getCodigo()) {
            return nodo.getDato();
        }
        if (codigo < nodo.getDato().getCodigo()) {
            return buscarRecursivo(nodo.getIzquierdo(), codigo);
        }
        return buscarRecursivo(nodo.getDerecho(), codigo);
    }

    private void recorridoInordenRecursivo(NodoBST nodo, List<Cliente> clientes) {
        if (nodo == null) {
            return;
        }
        recorridoInordenRecursivo(nodo.getIzquierdo(), clientes);
        clientes.add(nodo.getDato());
        recorridoInordenRecursivo(nodo.getDerecho(), clientes);
    }
}
