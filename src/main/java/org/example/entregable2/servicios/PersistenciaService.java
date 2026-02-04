package org.example.entregable2.servicios;

import org.example.entregable2.datos.EquipoDatos;
import org.example.entregable2.datos.JornadaDatos;
import org.example.entregable2.datos.PartidoDatos;
import org.example.entregable2.datos.TemporadaDatos;
import org.example.entregable2.dto.EquipoDTO;
import org.example.entregable2.dto.JornadaDTO;
import org.example.entregable2.dto.PartidoDTO;
import org.example.entregable2.dto.TemporadaDTO;
import org.example.entregable2.logica.Equipo;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.logica.Partido;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistenciaService {

    private static PersistenciaService instance;
    private final EquipoDatos equipoDatos;
    private final PartidoDatos partidoDatos;
    private final TemporadaDatos temporadaDatos;
    private final JornadaDatos jornadaDatos;

    private PersistenciaService() {
        this.equipoDatos = new EquipoDatos();
        this.partidoDatos = new PartidoDatos();
        this.temporadaDatos = new TemporadaDatos();
        this.jornadaDatos = new JornadaDatos();
    }

    public static PersistenciaService getInstance() {
        if (instance == null) {
            instance = new PersistenciaService();
        }
        return instance;
    }

    public void guardarLiga(Liga liga) {
        TemporadaDTO temporada = temporadaDatos.obtenerTemporadaActiva();
        if (temporada == null) {
            temporada = new TemporadaDTO(0, "Premier League 2024-2025", 2024, 2025);
            temporada.setActiva(true);
            int idTemporada = temporadaDatos.insertar(temporada);
            temporada.setIdTemporada(idTemporada);
        }

        List<Equipo> equipos = liga.getTodosLosEquipos();
        for (Equipo eq : equipos) {
            EquipoDTO dto = eq.toDTO();

            EquipoDTO existente = equipoDatos.obtenerEquipoPorCodigo(dto.getCodigo());
            if (existente != null) {
                dto.setIdEquipo(existente.getIdEquipo());
                equipoDatos.modificar(dto);
            } else {
                int idGen = equipoDatos.insertar(dto);
                dto.setIdEquipo(idGen);
            }
        }

        List<Partido> partidos = liga.getCalendario();
        if (partidos == null || partidos.isEmpty()) {
            return;
        }

        Map<Integer, JornadaDTO> jornadasCache = new HashMap<>();

        for (Partido p : partidos) {
            int numJornada = p.getJornada();

            JornadaDTO jornada = jornadasCache.get(numJornada);
            if (jornada == null) {
                jornada = jornadaDatos.obtenerPorTemporadaYNumero(temporada.getIdTemporada(), numJornada);
                if (jornada == null) {
                    jornada = new JornadaDTO(0, temporada.getIdTemporada(), numJornada, "Jornada " + numJornada);
                    int idJornada = jornadaDatos.insertar(jornada);
                    jornada.setIdJornada(idJornada);
                }
                jornadasCache.put(numJornada, jornada);
            }

            PartidoDTO dto = new PartidoDTO(
                    p.getId(),
                    jornada.getIdJornada(),
                    temporada.getIdTemporada(),
                    p.getLocal().getCodigo(),
                    p.getVisitante().getCodigo()
            );
            dto.setJornada(numJornada);
            dto.setFecha(p.getFecha());
            dto.setEstadio(p.getLocal().getEstadio());

            if (p.tieneResultado()) {
                dto.setGolesLocal(p.getGolesLocal());
                dto.setGolesVisitante(p.getGolesVisitante());
                dto.setEstado("FINALIZADO");
            } else {
                dto.setGolesLocal(0);
                dto.setGolesVisitante(0);
                dto.setEstado("PENDIENTE");
            }

            if (dto.getId() > 0) {
                partidoDatos.modificar(dto);
            } else {
                int idGen = partidoDatos.insertar(dto);
                p.setId(idGen);
            }
        }
    }

    public Liga cargarLiga(String nombreLiga) {
        Liga liga = new Liga(nombreLiga);

        List<EquipoDTO> equiposDTO = equipoDatos.listaEquipoActivos();
        for (EquipoDTO dto : equiposDTO) {
            Equipo equipo = Equipo.fromDTO(dto);
            liga.registrarEquipo(equipo);
        }

        List<PartidoDTO> partidosDTO = partidoDatos.listaPartidosActivos();
        if (partidosDTO != null && !partidosDTO.isEmpty()) {
            liga.cargarPartidosDesdeDTO(partidosDTO);
        }

        return liga;
    }

    public boolean existenDatos() {
        try {
            List<EquipoDTO> equipos = equipoDatos.listaEquipoActivos();
            return equipos != null && !equipos.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}