package org.example.entregable2.dto;

import javafx.beans.property.*;
import java.util.Date;


public class PartidoDTO {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    private final IntegerProperty jornada = new SimpleIntegerProperty(this, "jornada", 0);
    private final ObjectProperty<Date> fecha = new SimpleObjectProperty<>(this, "fecha", new Date());
    private final StringProperty equipoLocalCodigo = new SimpleStringProperty(this, "equipoLocalCodigo", "");
    private final StringProperty equipoVisitanteCodigo = new SimpleStringProperty(this, "equipoVisitanteCodigo", "");
    private final IntegerProperty golesLocal = new SimpleIntegerProperty(this, "golesLocal", -1);
    private final IntegerProperty golesVisitante = new SimpleIntegerProperty(this, "golesVisitante", -1);
    private final BooleanProperty tieneResultado = new SimpleBooleanProperty(this, "tieneResultado", false);

    public PartidoDTO() {
    }

    public PartidoDTO(int id, int jornada, String equipoLocalCodigo, String equipoVisitanteCodigo) {
        setId(id);
        setJornada(jornada);
        setEquipoLocalCodigo(equipoLocalCodigo);
        setEquipoVisitanteCodigo(equipoVisitanteCodigo);
    }

    /* ========================== ID ========================== */
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    /* ========================== JORNADA ========================== */
    public int getJornada() {
        return jornada.get();
    }

    public void setJornada(int jornada) {
        this.jornada.set(jornada);
    }

    public IntegerProperty jornadaProperty() {
        return jornada;
    }

    /* ========================== FECHA ========================== */
    public Date getFecha() {
        return fecha.get();
    }

    public void setFecha(Date fecha) {
        this.fecha.set(fecha);
    }

    public ObjectProperty<Date> fechaProperty() {
        return fecha;
    }

    /* ========================== EQUIPO LOCAL CÓDIGO ========================== */
    public String getEquipoLocalCodigo() {
        return equipoLocalCodigo.get();
    }

    public void setEquipoLocalCodigo(String equipoLocalCodigo) {
        this.equipoLocalCodigo.set(equipoLocalCodigo);
    }

    public StringProperty equipoLocalCodigoProperty() {
        return equipoLocalCodigo;
    }

    /* ========================== EQUIPO VISITANTE CÓDIGO ========================== */
    public String getEquipoVisitanteCodigo() {
        return equipoVisitanteCodigo.get();
    }

    public void setEquipoVisitanteCodigo(String equipoVisitanteCodigo) {
        this.equipoVisitanteCodigo.set(equipoVisitanteCodigo);
    }

    public StringProperty equipoVisitanteCodigoProperty() {
        return equipoVisitanteCodigo;
    }

    /* ========================== GOLES LOCAL ========================== */
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

    /* ========================== GOLES VISITANTE ========================== */
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

    /* ========================== TIENE RESULTADO ========================== */
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
    }

    @Override
    public String toString() {
        return "Partido #" + id.get() + " - Jornada " + jornada.get();
    }
}

