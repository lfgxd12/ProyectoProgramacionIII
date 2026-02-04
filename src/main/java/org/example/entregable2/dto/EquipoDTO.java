package org.example.entregable2.dto;

import javafx.beans.property.*;

public class EquipoDTO {

    private final IntegerProperty idEquipo = new SimpleIntegerProperty(this, "idEquipo", 0);
    private final StringProperty codigo = new SimpleStringProperty(this, "codigo", "");
    private final StringProperty nombre = new SimpleStringProperty(this, "nombre", "");
    private final StringProperty ciudad = new SimpleStringProperty(this, "ciudad", "");
    private final StringProperty estadio = new SimpleStringProperty(this, "estadio", "");
    private final IntegerProperty anioFundacion = new SimpleIntegerProperty(this, "anioFundacion", 0);
    private final BooleanProperty eliminado = new SimpleBooleanProperty(this, "eliminado", false);

    private final IntegerProperty pj = new SimpleIntegerProperty(this, "pj", 0);
    private final IntegerProperty g = new SimpleIntegerProperty(this, "g", 0);
    private final IntegerProperty e = new SimpleIntegerProperty(this, "e", 0);
    private final IntegerProperty p = new SimpleIntegerProperty(this, "p", 0);
    private final IntegerProperty gf = new SimpleIntegerProperty(this, "gf", 0);
    private final IntegerProperty gc = new SimpleIntegerProperty(this, "gc", 0);
    private final IntegerProperty dg = new SimpleIntegerProperty(this, "dg", 0);
    private final IntegerProperty pts = new SimpleIntegerProperty(this, "pts", 0);

    public EquipoDTO() {
    }

    public EquipoDTO(int idEquipo, String codigo, String nombre, String ciudad, String estadio, int anioFundacion) {
        setIdEquipo(idEquipo);
        setCodigo(codigo);
        setNombre(nombre);
        setCiudad(ciudad);
        setEstadio(estadio);
        setAnioFundacion(anioFundacion);
    }

    public int getIdEquipo() {
        return idEquipo.get();
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo.set(idEquipo);
    }

    public IntegerProperty idEquipoProperty() {
        return idEquipo;
    }

    public String getCodigo() {
        return codigo.get();
    }

    public void setCodigo(String codigo) {
        this.codigo.set(codigo);
    }

    public StringProperty codigoProperty() {
        return codigo;
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

    public String getCiudad() {
        return ciudad.get();
    }

    public void setCiudad(String ciudad) {
        this.ciudad.set(ciudad);
    }

    public StringProperty ciudadProperty() {
        return ciudad;
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

    public int getAnioFundacion() {
        return anioFundacion.get();
    }

    public void setAnioFundacion(int anioFundacion) {
        this.anioFundacion.set(anioFundacion);
    }

    public IntegerProperty anioFundacionProperty() {
        return anioFundacion;
    }

    public boolean isEliminado() {
        return eliminado.get();
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado.set(eliminado);
    }

    public BooleanProperty eliminadoProperty() {
        return eliminado;
    }

    public int getPj() {
        return pj.get();
    }

    public void setPj(int pj) {
        this.pj.set(pj);
    }

    public IntegerProperty pjProperty() {
        return pj;
    }

    public int getG() {
        return g.get();
    }

    public void setG(int g) {
        this.g.set(g);
    }

    public IntegerProperty gProperty() {
        return g;
    }

    public int getE() {
        return e.get();
    }

    public void setE(int e) {
        this.e.set(e);
    }

    public IntegerProperty eProperty() {
        return e;
    }

    public int getP() {
        return p.get();
    }

    public void setP(int p) {
        this.p.set(p);
    }

    public IntegerProperty pProperty() {
        return p;
    }

    public int getGf() {
        return gf.get();
    }

    public void setGf(int gf) {
        this.gf.set(gf);
    }

    public IntegerProperty gfProperty() {
        return gf;
    }

    public int getGc() {
        return gc.get();
    }

    public void setGc(int gc) {
        this.gc.set(gc);
    }

    public IntegerProperty gcProperty() {
        return gc;
    }

    public int getDg() {
        return dg.get();
    }

    public void setDg(int dg) {
        this.dg.set(dg);
    }

    public IntegerProperty dgProperty() {
        return dg;
    }

    public int getPts() {
        return pts.get();
    }

    public void setPts(int pts) {
        this.pts.set(pts);
    }

    public IntegerProperty ptsProperty() {
        return pts;
    }

    @Override
    public String toString() {
        if (isEliminado()) {
            return nombre.get() + " (Equipo Borrado)";
        }
        return nombre.get() + " (" + codigo.get() + ")";
    }
}

