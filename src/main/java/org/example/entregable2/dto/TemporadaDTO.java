package org.example.entregable2.dto;

import javafx.beans.property.*;
import java.util.Date;

public class TemporadaDTO {

    private final IntegerProperty idTemporada = new SimpleIntegerProperty(this, "idTemporada", 0);
    private final StringProperty nombre = new SimpleStringProperty(this, "nombre", "");
    private final IntegerProperty anioInicio = new SimpleIntegerProperty(this, "anioInicio", 0);
    private final IntegerProperty anioFin = new SimpleIntegerProperty(this, "anioFin", 0);
    private final ObjectProperty<Date> fechaInicio = new SimpleObjectProperty<>(this, "fechaInicio", null);
    private final ObjectProperty<Date> fechaFin = new SimpleObjectProperty<>(this, "fechaFin", null);
    private final BooleanProperty activa = new SimpleBooleanProperty(this, "activa", false);

    // Constructores
    public TemporadaDTO() {
    }

    public TemporadaDTO(int idTemporada, String nombre, int anioInicio, int anioFin) {
        setIdTemporada(idTemporada);
        setNombre(nombre);
        setAnioInicio(anioInicio);
        setAnioFin(anioFin);
    }

    // Getters y Setters
    public int getIdTemporada() {
        return idTemporada.get();
    }

    public void setIdTemporada(int idTemporada) {
        this.idTemporada.set(idTemporada);
    }

    public IntegerProperty idTemporadaProperty() {
        return idTemporada;
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

    public int getAnioInicio() {
        return anioInicio.get();
    }

    public void setAnioInicio(int anioInicio) {
        this.anioInicio.set(anioInicio);
    }

    public IntegerProperty anioInicioProperty() {
        return anioInicio;
    }

    public int getAnioFin() {
        return anioFin.get();
    }

    public void setAnioFin(int anioFin) {
        this.anioFin.set(anioFin);
    }

    public IntegerProperty anioFinProperty() {
        return anioFin;
    }

    public Date getFechaInicio() {
        return fechaInicio.get();
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio.set(fechaInicio);
    }

    public ObjectProperty<Date> fechaInicioProperty() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin.get();
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin.set(fechaFin);
    }

    public ObjectProperty<Date> fechaFinProperty() {
        return fechaFin;
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
        return nombre.get() + " (" + anioInicio.get() + "-" + anioFin.get() + ")";
    }
}

