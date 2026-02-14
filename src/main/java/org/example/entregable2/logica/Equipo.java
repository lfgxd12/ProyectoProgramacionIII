package org.example.entregable2.logica;
import org.example.entregable2.dto.EquipoDTO;

public class Equipo {

    private String nombre;
    private String ciudad;
    private String codigo;
    private String estadio;
    private String annioFundacion;
    private int pj;  // Partidos jugados
    private int g;   // Ganados
    private int e;   // Empatados
    private int p;   // Perdidos
    private int gf;  // Goles a favor
    private int gc;  // Goles en contra
    private int dg;  // Diferencia de goles
    private int pts; // Puntos
    private boolean eliminado; // Marca si el equipo fue eliminado

    public Equipo(String nombre, String ciudad, String codigo) {
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.codigo = codigo;
        this.pj = 0;
        this.g = 0;
        this.e = 0;
        this.p = 0;
        this.gf = 0;
        this.gc = 0;
        this.dg = 0;
        this.pts = 0;
        this.eliminado = false;
    }

    public void actualizarEstadisticas(int golesFavor, int golesContra) {
        this.gf += golesFavor;
        this.gc += golesContra;
        this.dg = this.gf - this.gc;
    }

    public void aplicarResultado(int golesFavor, int golesContra) {
        this.pj++;
        actualizarEstadisticas(golesFavor, golesContra);

        if (golesFavor > golesContra) {
            this.g++;
            this.pts += 3;
        } else if (golesFavor == golesContra) {
            this.e++;
            this.pts += 1;
        } else {
            this.p++;
        }
    }

    public void reiniciarTemporada() {
        this.pj = 0;
        this.g = 0;
        this.e = 0;
        this.p = 0;
        this.gf = 0;
        this.gc = 0;
        this.dg = 0;
        this.pts = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getPj() {
        return pj;
    }

    public int getG() {
        return g;
    }

    public int getE() {
        return e;
    }

    public int getP() {
        return p;
    }

    public int getGf() {
        return gf;
    }

    public int getGc() {
        return gc;
    }

    public int getDg() {
        return dg;
    }

    public int getPts() {
        return pts;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public String getEstadio() {
        return estadio;
    }

    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }

    public String getAnnioFundacion() {
        return annioFundacion;
    }

    public void setAnnioFundacion(String annioFundacion) {
        this.annioFundacion = annioFundacion;
    }

    @Override
    public String toString() {
        if (eliminado) {
            return nombre + " (Equipo Borrado)";
        }
        return nombre + " (" + codigo + ")";
    }

    public EquipoDTO toDTO() {
        EquipoDTO dto = new EquipoDTO(0, codigo, nombre, ciudad, estadio,
                annioFundacion != null ? Integer.parseInt(annioFundacion) : 0);
        dto.setEliminado(eliminado);
        dto.setPj(pj);
        dto.setG(g);
        dto.setE(e);
        dto.setP(p);
        dto.setGf(gf);
        dto.setGc(gc);
        dto.setDg(dg);
        dto.setPts(pts);
        return dto;
    }

    public static Equipo fromDTO(EquipoDTO dto) {
        Equipo equipo = new Equipo(dto.getNombre(), dto.getCiudad(), dto.getCodigo());
        equipo.setEstadio(dto.getEstadio());
        equipo.setAnnioFundacion(String.valueOf(dto.getAnioFundacion()));
        equipo.setEliminado(dto.isEliminado());

        equipo.pj = dto.getPj();
        equipo.g = dto.getG();
        equipo.e = dto.getE();
        equipo.p = dto.getP();
        equipo.gf = dto.getGf();
        equipo.gc = dto.getGc();
        equipo.dg = dto.getDg();
        equipo.pts = dto.getPts();

        return equipo;
    }
}

