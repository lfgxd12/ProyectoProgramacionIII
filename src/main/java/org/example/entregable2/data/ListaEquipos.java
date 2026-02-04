package org.example.entregable2.data;

import org.example.entregable2.logica.Equipo;

import java.util.ArrayList;
import java.util.List;

public class ListaEquipos {
    private NodoEquipo cabeza;
    private int tamanio;

    public ListaEquipos() {
        this.cabeza = null;
        this.tamanio = 0;
    }

    public void agregar(Equipo equipo) {
        NodoEquipo nuevoNodo = new NodoEquipo(equipo);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            NodoEquipo actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
        tamanio++;
    }

    public boolean eliminar(String codigo) {
        if (cabeza == null) {
            return false;
        }

        if (cabeza.getEquipo().getCodigo().equals(codigo)) {
            cabeza = cabeza.getSiguiente();
            tamanio--;
            return true;
        }

        NodoEquipo actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getEquipo().getCodigo().equals(codigo)) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamanio--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public Equipo buscarPorCodigo(String codigo) {
        NodoEquipo actual = cabeza;
        while (actual != null) {
            if (actual.getEquipo().getCodigo().equals(codigo)) {
                return actual.getEquipo();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public List<Equipo> obtenerTodos() {
        List<Equipo> equipos = new ArrayList<>();
        NodoEquipo actual = cabeza;
        while (actual != null) {
            equipos.add(actual.getEquipo());
            actual = actual.getSiguiente();
        }
        return equipos;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public void limpiar() {
        cabeza = null;
        tamanio = 0;
    }
}

