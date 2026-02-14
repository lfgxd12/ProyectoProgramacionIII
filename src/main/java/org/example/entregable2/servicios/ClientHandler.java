package org.example.entregable2.servicios;

import org.example.entregable2.datos.*;
import org.example.entregable2.dto.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final EquipoDatos equipoDao = new EquipoDatos();
    private final TemporadaDatos temporadaDao = new TemporadaDatos();
    private final JornadaDatos jornadaDao = new JornadaDatos();
    private final PartidoDatos partidoDao = new PartidoDatos();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

    public ClientHandler(Socket socket) { this.socket = socket; }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            String req;
            while ((req = in.readLine()) != null) {
                String res = procesar(req);
                out.write(res);
                out.write("\n");
                out.flush();
            }

        } catch (Exception e) {
            System.err.println("Cliente desconectado/error: " + e.getMessage());
        }
    }

    private String procesar(String req) {
        try {
            String[] p = req.split("\\|", -1);
            String cmd = p[0].trim().toUpperCase();

            // ========== COMANDOS DE EQUIPO ==========
            switch (cmd) {
                case "LIST": {
                    List<EquipoDTO> equipos = equipoDao.listaEquipoActivos();
                    return "OK|" + serializarListaEquipos(equipos);
                }
                case "CREATE": {
                    EquipoDTO e = new EquipoDTO();
                    e.setCodigo(p[1]);
                    e.setNombre(p[2]);
                    e.setCiudad(p[3]);
                    e.setEstadio(p[4]);
                    e.setAnioFundacion(Integer.parseInt(p[5]));
                    int id = equipoDao.insertar(e);
                    return (id > 0) ? "OK|ID=" + id : "ERR|No se pudo insertar";
                }
                case "UPDATE": {
                    EquipoDTO e = new EquipoDTO();
                    e.setIdEquipo(Integer.parseInt(p[1]));
                    e.setCodigo(p[2]);
                    e.setNombre(p[3]);
                    e.setCiudad(p[4]);
                    e.setEstadio(p[5]);
                    e.setAnioFundacion(Integer.parseInt(p[6]));
                    boolean ok = equipoDao.modificar(e);
                    return ok ? "OK|Actualizado" : "ERR|No se pudo actualizar";
                }
                case "DELETE": {
                    int id = Integer.parseInt(p[1]);
                    boolean ok = equipoDao.eliminar(id);
                    return ok ? "OK|Eliminado" : "ERR|No se pudo eliminar";
                }

                // ========== COMANDOS DE TEMPORADA ==========
                case "TEMPORADA_LIST": {
                    List<TemporadaDTO> temporadas = temporadaDao.listarTodas();
                    return "OK|" + serializarListaTemporadas(temporadas);
                }
                case "TEMPORADA_GET_ACTIVE": {
                    TemporadaDTO t = temporadaDao.obtenerTemporadaActiva();
                    return "OK|" + (t != null ? serializarTemporada(t) : "");
                }
                case "TEMPORADA_CREATE": {
                    TemporadaDTO t = new TemporadaDTO();
                    t.setNombre(p[1]);
                    t.setAnioInicio(Integer.parseInt(p[2]));
                    t.setAnioFin(Integer.parseInt(p[3]));
                    t.setFechaInicio(parseDate(p[4]));
                    t.setFechaFin(parseDate(p[5]));
                    t.setActiva("1".equals(p[6]));
                    int id = temporadaDao.insertar(t);
                    return (id > 0) ? "OK|ID=" + id : "ERR|No se pudo insertar";
                }
                case "TEMPORADA_UPDATE": {
                    TemporadaDTO t = new TemporadaDTO();
                    t.setIdTemporada(Integer.parseInt(p[1]));
                    t.setNombre(p[2]);
                    t.setAnioInicio(Integer.parseInt(p[3]));
                    t.setAnioFin(Integer.parseInt(p[4]));
                    t.setFechaInicio(parseDate(p[5]));
                    t.setFechaFin(parseDate(p[6]));
                    t.setActiva("1".equals(p[7]));
                    boolean ok = temporadaDao.modificar(t);
                    return ok ? "OK|Actualizado" : "ERR|No se pudo actualizar";
                }
                case "TEMPORADA_DELETE": {
                    int id = Integer.parseInt(p[1]);
                    boolean ok = temporadaDao.eliminar(id);
                    return ok ? "OK|Eliminado" : "ERR|No se pudo eliminar";
                }

                // ========== COMANDOS DE JORNADA ==========
                case "JORNADA_LIST_BY_TEMPORADA": {
                    int idTemporada = Integer.parseInt(p[1]);
                    List<JornadaDTO> jornadas = jornadaDao.listarPorTemporada(idTemporada);
                    return "OK|" + serializarListaJornadas(jornadas);
                }
                case "JORNADA_GET": {
                    int id = Integer.parseInt(p[1]);
                    JornadaDTO j = jornadaDao.obtenerPorId(id);
                    return "OK|" + (j != null ? serializarJornada(j) : "");
                }
                case "JORNADA_CREATE": {
                    JornadaDTO j = new JornadaDTO();
                    j.setIdTemporada(Integer.parseInt(p[1]));
                    j.setNumeroJornada(Integer.parseInt(p[2]));
                    int id = jornadaDao.insertar(j);
                    return (id > 0) ? "OK|ID=" + id : "ERR|No se pudo insertar";
                }
                case "JORNADA_UPDATE": {
                    JornadaDTO j = new JornadaDTO();
                    j.setIdJornada(Integer.parseInt(p[1]));
                    j.setIdTemporada(Integer.parseInt(p[2]));
                    j.setNumeroJornada(Integer.parseInt(p[3]));
                    boolean ok = jornadaDao.modificar(j);
                    return ok ? "OK|Actualizado" : "ERR|No se pudo actualizar";
                }
                case "JORNADA_DELETE": {
                    int id = Integer.parseInt(p[1]);
                    boolean ok = jornadaDao.eliminar(id);
                    return ok ? "OK|Eliminado" : "ERR|No se pudo eliminar";
                }

                // ========== COMANDOS DE PARTIDO ==========
                case "PARTIDO_LIST_BY_JORNADA": {
                    int idTemporada = Integer.parseInt(p[1]);
                    int jornada = Integer.parseInt(p[2]);
                    List<PartidoDTO> partidos = partidoDao.listaPartidosPorJornada(idTemporada, jornada);
                    return "OK|" + serializarListaPartidos(partidos);
                }
                case "PARTIDO_LIST_BY_TEMPORADA": {
                    int idTemporada = Integer.parseInt(p[1]);
                    List<PartidoDTO> partidos = partidoDao.listaPartidosPorTemporada(idTemporada);
                    return "OK|" + serializarListaPartidos(partidos);
                }
                case "PARTIDO_CREATE": {
                    PartidoDTO part = new PartidoDTO();
                    part.setIdJornada(Integer.parseInt(p[1]));
                    part.setIdTemporada(Integer.parseInt(p[2]));
                    part.setEquipoLocalCodigo(p[3]);
                    part.setEquipoVisitanteCodigo(p[4]);
                    part.setEstadio(p[5]);
                    part.setFecha(parseDateTime(p[6]));
                    int id = partidoDao.insertar(part);
                    return (id > 0) ? "OK|ID=" + id : "ERR|No se pudo insertar";
                }
                case "PARTIDO_RESULT": {
                    int idPartido = Integer.parseInt(p[1]);
                    int golesLocal = Integer.parseInt(p[2]);
                    int golesVisitante = Integer.parseInt(p[3]);

                    PartidoDTO partido = partidoDao.obtenerPartidoPorId(idPartido);
                    if (partido == null) {
                        return "ERR|Partido no encontrado";
                    }

                    partido.setGolesLocal(golesLocal);
                    partido.setGolesVisitante(golesVisitante);
                    partido.setEstado("FINALIZADO");

                    boolean ok = partidoDao.modificar(partido);
                    return ok ? "OK|Resultado registrado" : "ERR|No se pudo registrar resultado";
                }
                case "PARTIDO_UPDATE": {
                    PartidoDTO part = new PartidoDTO();
                    part.setId(Integer.parseInt(p[1]));
                    part.setIdJornada(Integer.parseInt(p[2]));
                    part.setIdTemporada(Integer.parseInt(p[3]));
                    part.setEquipoLocalCodigo(p[4]);
                    part.setEquipoVisitanteCodigo(p[5]);
                    part.setEstadio(p[6]);
                    part.setFecha(parseDateTime(p[7]));
                    part.setGolesLocal(Integer.parseInt(p[8]));
                    part.setGolesVisitante(Integer.parseInt(p[9]));
                    part.setEstado(p[10]);
                    boolean ok = partidoDao.modificar(part);
                    return ok ? "OK|Actualizado" : "ERR|No se pudo actualizar";
                }
                case "PARTIDO_DELETE": {
                    int id = Integer.parseInt(p[1]);
                    boolean ok = partidoDao.eliminar(id);
                    return ok ? "OK|Eliminado" : "ERR|No se pudo eliminar";
                }

                default:
                    return "ERR|Comando no soportado: " + cmd;
            }

        } catch (Exception ex) {
            return "ERR|" + ex.getMessage();
        }
    }

    private String serializarListaEquipos(List<EquipoDTO> equipos) {
        // Cada equipo: id;codigo;nombre;ciudad;estadio;anio  y separados por ||
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < equipos.size(); i++) {
            EquipoDTO e = equipos.get(i);
            sb.append(e.getIdEquipo()).append(";")
                    .append(clean(e.getCodigo())).append(";")
                    .append(clean(e.getNombre())).append(";")
                    .append(clean(e.getCiudad())).append(";")
                    .append(clean(e.getEstadio())).append(";")
                    .append(e.getAnioFundacion());
            if (i < equipos.size() - 1) sb.append("||");
        }
        return sb.toString();
    }

    private String serializarListaTemporadas(List<TemporadaDTO> temporadas) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < temporadas.size(); i++) {
            sb.append(serializarTemporada(temporadas.get(i)));
            if (i < temporadas.size() - 1) sb.append("||");
        }
        return sb.toString();
    }

    private String serializarTemporada(TemporadaDTO t) {
        // id;nombre;anioInicio;anioFin;fechaInicio;fechaFin;activa
        return t.getIdTemporada() + ";" + clean(t.getNombre()) + ";" +
               t.getAnioInicio() + ";" + t.getAnioFin() + ";" +
               formatDate(t.getFechaInicio()) + ";" + formatDate(t.getFechaFin()) + ";" +
               (t.isActiva() ? "1" : "0");
    }

    private String serializarListaJornadas(List<JornadaDTO> jornadas) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jornadas.size(); i++) {
            sb.append(serializarJornada(jornadas.get(i)));
            if (i < jornadas.size() - 1) sb.append("||");
        }
        return sb.toString();
    }

    private String serializarJornada(JornadaDTO j) {
        // id;idTemporada;numeroJornada
        return j.getIdJornada() + ";" + j.getIdTemporada() + ";" + j.getNumeroJornada();
    }

    private String serializarListaPartidos(List<PartidoDTO> partidos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partidos.size(); i++) {
            sb.append(serializarPartido(partidos.get(i)));
            if (i < partidos.size() - 1) sb.append("||");
        }
        return sb.toString();
    }

    private String serializarPartido(PartidoDTO p) {
        // id;idJornada;idTemporada;equipoLocalCodigo;equipoVisitanteCodigo;estadio;fecha;golesLocal;golesVisitante;estado
        return p.getId() + ";" + p.getIdJornada() + ";" + p.getIdTemporada() + ";" +
               clean(p.getEquipoLocalCodigo()) + ";" + clean(p.getEquipoVisitanteCodigo()) + ";" +
               clean(p.getEstadio()) + ";" + formatDateTime(p.getFecha()) + ";" +
               (p.getGolesLocal() >= 0 ? p.getGolesLocal() : "") + ";" +
               (p.getGolesVisitante() >= 0 ? p.getGolesVisitante() : "") + ";" +
               clean(p.getEstado());
    }

    private String formatDate(Date date) {
        return date == null ? "" : sdfDate.format(date);
    }

    private String formatDateTime(Date date) {
        return date == null ? "" : sdf.format(date);
    }

    private Date parseDate(String dateStr) {
        try {
            return (dateStr == null || dateStr.isBlank()) ? null : sdfDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private Date parseDateTime(String dateStr) {
        try {
            return (dateStr == null || dateStr.isBlank()) ? null : sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private String clean(String s) {
        if (s == null) return "";
        return s.replace("|", " ").replace(";", " ").replace("||", " ");
    }
}
