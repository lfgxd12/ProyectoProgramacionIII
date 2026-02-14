package org.example.entregable2.logica;

import org.example.entregable2.client.EquipoServiceClientSocket;
import org.example.entregable2.dto.EquipoDTO;

import java.util.List;

/**
 * Capa de lógica de negocio para Equipos.
 * Se comunica con el servidor mediante sockets.
 * NO accede directamente a la base de datos.
 */
public class EquipoLogica {

    private final String host = "127.0.0.1";
    private final int port = 5050;

    /**
     * Carga todos los equipos activos desde el servidor
     */
    public List<EquipoDTO> cargarEquipos() throws Exception {
        try (var service = new EquipoServiceClientSocket(host, port)) {
            return service.listar();
        }
    }

    /**
     * Crea un nuevo equipo en el servidor
     * @return ID generado del equipo
     */
    public int crearEquipo(EquipoDTO equipo) throws Exception {
        try (var service = new EquipoServiceClientSocket(host, port)) {
            return service.crear(equipo);
        }
    }

    /**
     * Actualiza un equipo existente
     */
    public void actualizarEquipo(EquipoDTO equipo) throws Exception {
        try (var service = new EquipoServiceClientSocket(host, port)) {
            service.actualizar(equipo);
        }
    }

    /**
     * Elimina (borrado lógico) un equipo
     */
    public void eliminarEquipo(int idEquipo) throws Exception {
        try (var service = new EquipoServiceClientSocket(host, port)) {
            service.eliminar(idEquipo);
        }
    }

    /**
     * Convierte de Equipo (lógica) a EquipoDTO para envío al servidor
     */
    public static EquipoDTO equipoToDTO(Equipo equipo) {
        return equipo.toDTO();
    }

    /**
     * Convierte de EquipoDTO (recibido del servidor) a Equipo (lógica)
     */
    public static Equipo dtoToEquipo(EquipoDTO dto) {
        return Equipo.fromDTO(dto);
    }
}

