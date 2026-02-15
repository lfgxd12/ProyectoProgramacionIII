package org.example.entregable2.server;

import org.example.entregable2.datos.*;
import org.example.entregable2.dto.*;
import org.example.entregable2.logica.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class ClientHandler implements Runnable {
    private final Socket socket;
    private final EquipoDatos equipoDao;
    private final PartidoDatos partidoDao;
    private final TemporadaDatos temporadaDao;
    private final JornadaDatos jornadaDao;
    private final Liga liga;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.equipoDao = new EquipoDatos();
        this.partidoDao = new PartidoDatos();
        this.temporadaDao = new TemporadaDatos();
        this.jornadaDao = new JornadaDatos();
        this.liga = new Liga("Premier League");
    }

    @Override
    public void run() {
        String clientInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            System.out.println(clientInfo + "] Conexión establecida");

            String req;
            while ((req = in.readLine()) != null) {
                System.out.println(clientInfo + "] Solicitud: " + req);

                String res = procesar(req);

                System.out.println(clientInfo + "] Respuesta: " +
                    (res.length() > 100 ? res.substring(0, 100) + "..." : res));

                out.write(res);
                out.write("\n");
                out.flush();
            }
        } catch (Exception e) {
            System.err.println(clientInfo + "] Error: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println(clientInfo + "] Desconectado");
            } catch (IOException e) {
                System.err.println("Error cerrando socket: " + e.getMessage());
            }
        }
    }

    private String procesar(String req) {
        try {
            String[] p = req.split("\\|", -1);
            String cmd = p[0].trim().toUpperCase();

            switch (cmd) {
                case "LIST":
                    return procesarListarEquipos();

                case "GET":
                    return procesarObtenerEquipo(p[1]);

                case "CREATE":
                    return procesarCrearEquipo(p);

                case "UPDATE":
                    return procesarActualizarEquipo(p);

                case "DELETE":
                    return procesarEliminarEquipo(p[1]);

                case "PARTIDO_LIST":
                    return procesarListarPartidos();

                case "PARTIDO_GET":
                    return procesarObtenerPartido(Integer.parseInt(p[1]));

                case "PARTIDO_JORNADA":
                    return procesarObtenerPartidosJornada(Integer.parseInt(p[1]));

                case "PARTIDO_REGISTRAR":
                    return procesarRegistrarResultado(p);

                case "CALENDARIO_GENERAR":
                    return procesarGenerarCalendario(Boolean.parseBoolean(p[1]));

                case "CALENDARIO_OBTENER":
                    return procesarObtenerCalendario();

                case "TABLA_POSICIONES":
                    return procesarTablaPosiciones();

                case "TEMPORADA_ACTIVA":
                    return procesarObtenerTemporadaActiva();

                case "TEMPORADA_CREAR":
                    return procesarCrearTemporada(p);

                default:
                    return "ERR|Comando no soportado: " + cmd;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return "ERR|Parámetros insuficientes para el comando";
        } catch (NumberFormatException e) {
            return "ERR|Formato numérico inválido: " + e.getMessage();
        } catch (Exception ex) {
            return "ERR|" + ex.getMessage();
        }
    }


    private String procesarListarEquipos() {
        List<EquipoDTO> equipos = equipoDao.listarActivos();
        return "OK|" + serializarListaEquipos(equipos);
    }

    private String procesarObtenerEquipo(String codigo) {
        EquipoDTO equipo = equipoDao.obtenerEquipoPorCodigo(codigo);
        if (equipo == null) {
            return "ERR|Equipo no encontrado con código: " + codigo;
        }
        return "OK|" + serializarEquipo(equipo);
    }

    private String procesarCrearEquipo(String[] p) {
        if (p.length < 6) {
            return "ERR|Faltan parámetros para crear equipo";
        }

        EquipoDTO existente = equipoDao.obtenerEquipoPorCodigo(p[1]);
        if (existente != null && !existente.isEliminado()) {
            return "ERR|Ya existe un equipo con el código: " + p[1];
        }

        EquipoDTO equipo = new EquipoDTO();
        equipo.setCodigo(p[1]);
        equipo.setNombre(p[2]);
        equipo.setCiudad(p[3]);
        equipo.setEstadio(p[4]);
        equipo.setAnioFundacion(Integer.parseInt(p[5]));
        equipo.setEliminado(false);

        int id = equipoDao.insertar(equipo);
        return (id > 0) ? "OK|ID=" + id : "ERR|No se pudo insertar el equipo";
    }

    private String procesarActualizarEquipo(String[] p) {
        if (p.length < 7) {
            return "ERR|Faltan parámetros para actualizar equipo";
        }

        EquipoDTO equipo = new EquipoDTO();
        equipo.setIdEquipo(Integer.parseInt(p[1]));
        equipo.setCodigo(p[2]);
        equipo.setNombre(p[3]);
        equipo.setCiudad(p[4]);
        equipo.setEstadio(p[5]);
        equipo.setAnioFundacion(Integer.parseInt(p[6]));

        boolean ok = equipoDao.modificar(equipo);
        return ok ? "OK|Actualizado" : "ERR|No se pudo actualizar el equipo";
    }

    private String procesarEliminarEquipo(String codigo) {
        EquipoDTO equipo = equipoDao.obtenerEquipoPorCodigo(codigo);
        if (equipo == null) {
            return "ERR|Equipo no encontrado";
        }

        boolean ok = equipoDao.eliminar(equipo.getIdEquipo());
        return ok ? "OK|Eliminado" : "ERR|No se pudo eliminar el equipo";
    }


    private String procesarListarPartidos() {
        TemporadaDTO temporada = temporadaDao.obtenerTemporadaActiva();
        if (temporada == null) {
            return "OK|";
        }

        List<PartidoDTO> partidos = partidoDao.listaPartidosPorTemporada(temporada.getIdTemporada());
        return "OK|" + serializarListaPartidos(partidos);
    }

    private String procesarObtenerPartido(int id) {
        PartidoDTO partido = partidoDao.obtenerPartidoPorId(id);
        if (partido == null) {
            return "ERR|Partido no encontrado con ID: " + id;
        }
        return "OK|" + serializarPartido(partido);
    }

    private String procesarObtenerPartidosJornada(int numJornada) {
        TemporadaDTO temporada = temporadaDao.obtenerTemporadaActiva();
        if (temporada == null) {
            return "ERR|No hay temporada activa";
        }

        JornadaDTO jornada = jornadaDao.obtenerPorTemporadaYNumero(temporada.getIdTemporada(), numJornada);
        if (jornada == null) {
            return "ERR|Jornada no encontrada: " + numJornada;
        }

        List<PartidoDTO> partidos = partidoDao.listaPartidosPorJornada(temporada.getIdTemporada(), numJornada);
        return "OK|" + serializarListaPartidos(partidos);
    }

    private String procesarRegistrarResultado(String[] p) {
        if (p.length < 4) {
            return "ERR|Faltan parámetros para registrar resultado";
        }

        int id = Integer.parseInt(p[1]);
        int golesLocal = Integer.parseInt(p[2]);
        int golesVisitante = Integer.parseInt(p[3]);

        PartidoDTO partido = partidoDao.obtenerPartidoPorId(id);
        if (partido == null) {
            return "ERR|Partido no encontrado";
        }

        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        partido.setEstado("FINALIZADO");
        partido.setTieneResultado(true);

        boolean ok = partidoDao.modificar(partido);

        if (ok) {
            actualizarEstadisticasEquipos(partido);
            return "OK|Resultado registrado";
        } else {
            return "ERR|No se pudo registrar el resultado";
        }
    }

    private void actualizarEstadisticasEquipos(PartidoDTO partido) {
        EquipoDTO local = equipoDao.obtenerEquipoPorCodigo(partido.getEquipoLocalCodigo());
        EquipoDTO visitante = equipoDao.obtenerEquipoPorCodigo(partido.getEquipoVisitanteCodigo());

        if (local != null && visitante != null) {
            local.setPj(local.getPj() + 1);
            local.setGf(local.getGf() + partido.getGolesLocal());
            local.setGc(local.getGc() + partido.getGolesVisitante());
            local.setDg(local.getGf() - local.getGc());

            visitante.setPj(visitante.getPj() + 1);
            visitante.setGf(visitante.getGf() + partido.getGolesVisitante());
            visitante.setGc(visitante.getGc() + partido.getGolesLocal());
            visitante.setDg(visitante.getGf() - visitante.getGc());

            if (partido.getGolesLocal() > partido.getGolesVisitante()) {
                local.setG(local.getG() + 1);
                local.setPts(local.getPts() + 3);
                visitante.setP(visitante.getP() + 1);
            } else if (partido.getGolesLocal() < partido.getGolesVisitante()) {
                visitante.setG(visitante.getG() + 1);
                visitante.setPts(visitante.getPts() + 3);
                local.setP(local.getP() + 1);
            } else {
                local.setE(local.getE() + 1);
                local.setPts(local.getPts() + 1);
                visitante.setE(visitante.getE() + 1);
                visitante.setPts(visitante.getPts() + 1);
            }

            equipoDao.modificar(local);
            equipoDao.modificar(visitante);
        }
    }


    private String procesarGenerarCalendario(boolean idaVuelta) {
        try {
            List<EquipoDTO> equiposDTO = equipoDao.listarActivos();
            if (equiposDTO.size() < 2) {
                return "ERR|Se necesitan al menos 2 equipos para generar el calendario";
            }

            liga.limpiarEquipos();
            for (EquipoDTO dto : equiposDTO) {
                Equipo eq = new Equipo(dto.getNombre(), dto.getCiudad(), dto.getCodigo());
                eq.setEstadio(dto.getEstadio());
                eq.setAnnioFundacion(String.valueOf(dto.getAnioFundacion()));
                liga.registrarEquipo(eq);
            }

            liga.generarCalendario(idaVuelta);

            TemporadaDTO temporada = temporadaDao.obtenerTemporadaActiva();
            if (temporada == null) {
                temporada = new TemporadaDTO(0, "Premier League 2024-2025", 2024, 2025);
                temporada.setActiva(true);
                int idTemp = temporadaDao.insertar(temporada);
                temporada.setIdTemporada(idTemp);
            }

            List<Partido> partidos = liga.getCalendario();
            int partidosGuardados = 0;

            for (Partido p : partidos) {
                JornadaDTO jornada = jornadaDao.obtenerPorTemporadaYNumero(
                    temporada.getIdTemporada(), p.getJornada());

                if (jornada == null) {
                    jornada = new JornadaDTO(0, temporada.getIdTemporada(),
                        p.getJornada(), "Jornada " + p.getJornada());
                    int idJor = jornadaDao.insertar(jornada);
                    jornada.setIdJornada(idJor);
                }

                PartidoDTO dto = new PartidoDTO();
                dto.setId(p.getId());
                dto.setIdJornada(jornada.getIdJornada());
                dto.setIdTemporada(temporada.getIdTemporada());
                dto.setJornada(p.getJornada());
                dto.setFecha(p.getFecha());
                dto.setEquipoLocalCodigo(p.getLocal().getCodigo());
                dto.setEquipoVisitanteCodigo(p.getVisitante().getCodigo());
                dto.setEstadio(p.getLocal().getEstadio());
                dto.setGolesLocal(0);
                dto.setGolesVisitante(0);
                dto.setEstado("PENDIENTE");

                int idPartido = partidoDao.insertar(dto);
                if (idPartido > 0) {
                    partidosGuardados++;
                }
            }

            return "OK|Calendario generado: " + partidosGuardados + " partidos";

        } catch (Exception e) {
            return "ERR|Error generando calendario: " + e.getMessage();
        }
    }

    private String procesarObtenerCalendario() {
        TemporadaDTO temporada = temporadaDao.obtenerTemporadaActiva();
        if (temporada == null) {
            return "OK|"; // Sin calendario
        }

        List<PartidoDTO> partidos = partidoDao.listaPartidosPorTemporada(temporada.getIdTemporada());
        return "OK|" + serializarListaPartidos(partidos);
    }


    private String procesarTablaPosiciones() {
        List<EquipoDTO> equipos = equipoDao.listarActivos();

        equipos.sort((e1, e2) -> {
            int cmpPts = Integer.compare(e2.getPts(), e1.getPts());
            if (cmpPts != 0) return cmpPts;

            int cmpDg = Integer.compare(e2.getDg(), e1.getDg());
            if (cmpDg != 0) return cmpDg;

            return Integer.compare(e2.getGf(), e1.getGf());
        });

        return "OK|" + serializarTablaPosiciones(equipos);
    }


    private String procesarObtenerTemporadaActiva() {
        TemporadaDTO temporada = temporadaDao.obtenerTemporadaActiva();
        if (temporada == null) {
            return "ERR|No hay temporada activa";
        }
        return "OK|" + serializarTemporada(temporada);
    }

    private String procesarCrearTemporada(String[] p) {
        if (p.length < 4) {
            return "ERR|Faltan parámetros para crear temporada";
        }

        TemporadaDTO temporada = new TemporadaDTO();
        temporada.setNombre(p[1]);
        temporada.setAnioInicio(Integer.parseInt(p[2]));
        temporada.setAnioFin(Integer.parseInt(p[3]));
        temporada.setActiva(true);

        int id = temporadaDao.insertar(temporada);
        return (id > 0) ? "OK|ID=" + id : "ERR|No se pudo crear la temporada";
    }


    private String serializarListaEquipos(List<EquipoDTO> equipos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < equipos.size(); i++) {
            sb.append(serializarEquipo(equipos.get(i)));
            if (i < equipos.size() - 1) sb.append("||");
        }
        return sb.toString();
    }

    private String serializarEquipo(EquipoDTO e) {
        return clean(e.getCodigo()) + ";" +
               clean(e.getNombre()) + ";" +
               clean(e.getCiudad()) + ";" +
               clean(e.getEstadio()) + ";" +
               e.getAnioFundacion() + ";" +
               e.getPj() + ";" +
               e.getG() + ";" +
               e.getE() + ";" +
               e.getP() + ";" +
               e.getGf() + ";" +
               e.getGc() + ";" +
               e.getDg() + ";" +
               e.getPts();
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
        return p.getId() + ";" +
               p.getJornada() + ";" +
               clean(p.getEquipoLocalCodigo()) + ";" +
               clean(p.getEquipoVisitanteCodigo()) + ";" +
               p.getGolesLocal() + ";" +
               p.getGolesVisitante() + ";" +
               clean(p.getEstado());
    }

    private String serializarTablaPosiciones(List<EquipoDTO> equipos) {
        return serializarListaEquipos(equipos);
    }

    private String serializarTemporada(TemporadaDTO t) {
        return t.getIdTemporada() + ";" +
               clean(t.getNombre()) + ";" +
               t.getAnioInicio() + ";" +
               t.getAnioFin();
    }

    private String clean(String s) {
        if (s == null) return "";
        return s.replace("|", " ").replace(";", " ").replace("||", " ").trim();
    }
}

