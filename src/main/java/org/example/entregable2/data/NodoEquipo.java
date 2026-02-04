package org.example.entregable2.data;

import org.example.entregable2.logica.Equipo;

public class NodoEquipo {
    private Equipo equipo;
    private NodoEquipo siguiente;

    public NodoEquipo(Equipo equipo) {
        this.equipo = equipo;
        this.siguiente = null;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public NodoEquipo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoEquipo siguiente) {
        this.siguiente = siguiente;
    }
}

