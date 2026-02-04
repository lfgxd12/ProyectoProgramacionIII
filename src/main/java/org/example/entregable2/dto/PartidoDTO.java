package org.example.entregable2.dto;

import javafx.beans.property.*;
import java.util.Date;

public class PartidoDTO {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    private final IntegerProperty idJornada = new SimpleIntegerProperty(this, "idJornada", 0);
    private final IntegerProperty idTemporada = new SimpleIntegerProperty(this, "idTemporada", 0);
    private final IntegerProperty jornada = new SimpleIntegerProperty(this, "jornada", 0); // Mantener por compatibilidad
    private final ObjectProperty<Date> fecha = new SimpleObjectProperty<>(this, "fecha", new Date());
    private final StringProperty equipoLocalCodigo = new SimpleStringProperty(this, "equipoLocalCodigo", "");
    private final StringProperty equipoVisitanteCodigo = new SimpleStringProperty(this, "equipoVisitanteCodigo", "");
    private final StringProperty estadio = new SimpleStringProperty(this, "estadio", "");
    private final IntegerProperty golesLocal = new SimpleIntegerProperty(this, "golesLocal", -1);
    private final IntegerProperty golesVisitante = new SimpleIntegerProperty(this, "golesVisitante", -1);
    private final StringProperty estado = new SimpleStringProperty(this, "estado", "PENDIENTE");
    private final BooleanProperty tieneResultado = new SimpleBooleanProperty(this, "tieneResultado", false);

    public PartidoDTO() {
    }

    public PartidoDTO(int id, int jornada, String equipoLocalCodigo, String equipoVisitanteCodigo) {
        setId(id);
        setJornada(jornada);
        setEquipoLocalCodigo(equipoLocalCodigo);
        setEquipoVisitanteCodigo(equipoVisitanteCodigo);
    }

    public PartidoDTO(int id, int idJornada, int idTemporada, String equipoLocalCodigo, String equipoVisitanteCodigo) {
        setId(id);
        setIdJornada(idJornada);
        setIdTemporada(idTemporada);
        setEquipoLocalCodigo(equipoLocalCodigo);
        setEquipoVisitanteCodigo(equipoVisitanteCodigo);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
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

    public int getJornada() {
        return jornada.get();
    }

    public void setJornada(int jornada) {
        this.jornada.set(jornada);
    }

    public IntegerProperty jornadaProperty() {
        return jornada;
    }

    public Date getFecha() {
        return fecha.get();
    }

    public void setFecha(Date fecha) {
        this.fecha.set(fecha);
    }

    public ObjectProperty<Date> fechaProperty() {
        return fecha;
    }

    public String getEquipoLocalCodigo() {
        return equipoLocalCodigo.get();
    }

    public void setEquipoLocalCodigo(String equipoLocalCodigo) {
        this.equipoLocalCodigo.set(equipoLocalCodigo);
    }

    public StringProperty equipoLocalCodigoProperty() {
        return equipoLocalCodigo;
    }

    public String getEquipoVisitanteCodigo() {
        return equipoVisitanteCodigo.get();
    }

    public void setEquipoVisitanteCodigo(String equipoVisitanteCodigo) {
        this.equipoVisitanteCodigo.set(equipoVisitanteCodigo);
    }

    public StringProperty equipoVisitanteCodigoProperty() {
        return equipoVisitanteCodigo;
    }

    public String getEstadio() {
        return estadio.get();
    }

    public void setEstadio(String estadio) {
        this.estadio.set(estadio);
    }

    public StringProperty estadioProperty() {
        return estadio;
    }

    public int getGolesLocal() {
        return golesLocal.get();
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal.set(golesLocal);
        updateTieneResultado();
    }

    public IntegerProperty golesLocalProperty() {
        return golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante.get();
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante.set(golesVisitante);
        updateTieneResultado();
    }

    public IntegerProperty golesVisitanteProperty() {
        return golesVisitante;
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

    public boolean isTieneResultado() {
        return tieneResultado.get();
    }

    public void setTieneResultado(boolean tieneResultado) {
        this.tieneResultado.set(tieneResultado);
    }

    public BooleanProperty tieneResultadoProperty() {
        return tieneResultado;
    }

    private void updateTieneResultado() {
        setTieneResultado(golesLocal.get() >= 0 && golesVisitante.get() >= 0);
        if (isTieneResultado()) {
            setEstado("FINALIZADO");
        }
    }

    @Override
    public String toString() {
        return "Partido #" + id.get() + " - Jornada " + jornada.get();
    }
}

