package org.example.entregable2.dto;

import javafx.beans.property.*;
import java.util.Date;

public class JornadaDTO {

    private final IntegerProperty idJornada = new SimpleIntegerProperty(this, "idJornada", 0);
    private final IntegerProperty idTemporada = new SimpleIntegerProperty(this, "idTemporada", 0);
    private final IntegerProperty numeroJornada = new SimpleIntegerProperty(this, "numeroJornada", 0);
    private final StringProperty nombre = new SimpleStringProperty(this, "nombre", "");
    private final ObjectProperty<Date> fechaProgramada = new SimpleObjectProperty<>(this, "fechaProgramada", null);
    private final StringProperty estado = new SimpleStringProperty(this, "estado", "PENDIENTE");
    private final BooleanProperty activa = new SimpleBooleanProperty(this, "activa", true);

    public JornadaDTO() {
    }

    public JornadaDTO(int idJornada, int idTemporada, int numeroJornada) {
        setIdJornada(idJornada);
        setIdTemporada(idTemporada);
        setNumeroJornada(numeroJornada);
        setNombre("Jornada " + numeroJornada);
    }

    public JornadaDTO(int idJornada, int idTemporada, int numeroJornada, String nombre) {
        this(idJornada, idTemporada, numeroJornada);
        setNombre(nombre);
    }

    public int getIdJornada() {
        return idJornada.get();
    }

    public void setIdJornada(int idJornada) {
        this.idJornada.set(idJornada);
    }

    public IntegerProperty idJornadaProperty() {
        return idJornada;
    }

    public int getIdTemporada() {
        return idTemporada.get();
    }

    public void setIdTemporada(int idTemporada) {
        this.idTemporada.set(idTemporada);
    }

    public IntegerProperty idTemporadaProperty() {
        return idTemporada;
    }

    public int getNumeroJornada() {
        return numeroJornada.get();
    }

    public void setNumeroJornada(int numeroJornada) {
        this.numeroJornada.set(numeroJornada);
    }

    public IntegerProperty numeroJornadaProperty() {
        return numeroJornada;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public Date getFechaProgramada() {
        return fechaProgramada.get();
    }

    public void setFechaProgramada(Date fechaProgramada) {
        this.fechaProgramada.set(fechaProgramada);
    }

    public ObjectProperty<Date> fechaProgramadaProperty() {
        return fechaProgramada;
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public boolean isActiva() {
        return activa.get();
    }

    public void setActiva(boolean activa) {
        this.activa.set(activa);
    }

    public BooleanProperty activaProperty() {
        return activa;
    }

    @Override
    public String toString() {
        return nombre.get() + " (" + estado.get() + ")";
    }
}

