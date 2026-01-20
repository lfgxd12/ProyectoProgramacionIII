package org.example.entregable2.servicios;

import org.example.entregable2.datos.EquipoDatos;
import org.example.entregable2.datos.PartidoDatos;
import org.example.entregable2.dto.EquipoDTO;
import org.example.entregable2.dto.PartidoDTO;
import org.example.entregable2.logica.Equipo;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.logica.Partido;

import java.util.ArrayList;
import java.util.List;

public class PersistenciaService {

    private static PersistenciaService instance;
    private final EquipoDatos equipoDatos;
    private final PartidoDatos partidoDatos;
    private static final String RUTA_EQUIPOS = "datos/equipos.xml";
    private static final String RUTA_PARTIDOS = "datos/partidos.xml";

    private PersistenciaService() {
        this.equipoDatos = new EquipoDatos(RUTA_EQUIPOS);
        this.partidoDatos = new PartidoDatos(RUTA_PARTIDOS);
    }

    public static PersistenciaService getInstance() {
        if (instance == null) {
            instance = new PersistenciaService();
        }
        return instance;
    }

    public void guardarLiga(Liga liga) throws Exception {
        List<Equipo> equipos = liga.getTodosLosEquipos();
        List<EquipoDTO> equiposDTO = new ArrayList<>();

        for (Equipo eq : equipos) {
            equiposDTO.add(eq.toDTO());
        }

        equipoDatos.guardar(equiposDTO);

        List<Partido> partidos = liga.getCalendario();
        List<PartidoDTO> partidosDTO = new ArrayList<>();

        for (Partido p : partidos) {
            PartidoDTO dto = new PartidoDTO(p.getId(), p.getJornada(),
                p.getLocal().getCodigo(), p.getVisitante().getCodigo());
            dto.setFecha(p.getFecha());
            if (p.tieneResultado()) {
                dto.setGolesLocal(p.getGolesLocal());
                dto.setGolesVisitante(p.getGolesVisitante());
            }
            partidosDTO.add(dto);
        }

        partidoDatos.guardar(partidosDTO);
    }

    public Liga cargarLiga(String nombreLiga) throws Exception {
        Liga liga = new Liga(nombreLiga);

        List<EquipoDTO> equiposDTO = equipoDatos.cargar();
        for (EquipoDTO dto : equiposDTO) {
            Equipo equipo = Equipo.fromDTO(dto);
            liga.registrarEquipo(equipo);
        }

        List<PartidoDTO> partidosDTO = partidoDatos.cargar();
        if (!partidosDTO.isEmpty()) {
            liga.cargarPartidosDesdeDTO(partidosDTO);
        }

        return liga;
    }

    public boolean existenDatos() {
        try {
            List<EquipoDTO> equipos = equipoDatos.cargar();
            return equipos != null && !equipos.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}

