package org.example.entregable2.logica;

import java.util.Date;

public class Partido {
    private int id;
    private Date fecha;
    private int jornada;
    private Equipo local;
    private Equipo visitante;
    private Integer golesLocal;
    private Integer golesVisitante;
    private boolean esAmistoso;

    public Partido(Equipo local, Equipo visitante, Date fecha, int jornada) {
        this.local = local;
        this.visitante = visitante;
        this.fecha = fecha;
        this.jornada = jornada;
        this.golesLocal = null;
        this.golesVisitante = null;
        this.esAmistoso = false;
    }

    public void registrarResultado(int golesLocal, int golesVisitante) {
        if (tieneResultado()) {
            throw new IllegalStateException("Este partido ya tiene un resultado registrado");
        }
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
    }

    public boolean tieneResultado() {
        return golesLocal != null && golesVisitante != null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getJornada() {
        return jornada;
    }

    public void setJornada(int jornada) {
        this.jornada = jornada;
    }

    public Equipo getLocal() {
        return local;
    }

    public void setLocal(Equipo local) {
        this.local = local;
    }

    public Equipo getVisitante() {
        return visitante;
    }

    public void setVisitante(Equipo visitante) {
        this.visitante = visitante;
    }

    public Integer getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(Integer golesLocal) {
        this.golesLocal = golesLocal;
    }

    public Integer getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(Integer golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public boolean isEsAmistoso() {
        return esAmistoso;
    }

    public void setEsAmistoso(boolean esAmistoso) {
        this.esAmistoso = esAmistoso;
    }

    @Override
    public String toString() {
        String resultado = tieneResultado() ?
            " [" + golesLocal + "-" + golesVisitante + "]" : " [Pendiente]";
        return local.getNombre() + " vs " + visitante.getNombre() + resultado;
    }
}

