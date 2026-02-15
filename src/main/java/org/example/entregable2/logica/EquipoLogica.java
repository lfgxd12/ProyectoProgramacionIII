package org.example.entregable2.logica;

import org.example.entregable2.client.EquipoServiceClientSocket;
import org.example.entregable2.client.SocketConfig;
import org.example.entregable2.dto.EquipoDTO;

import java.util.List;

/**
 * Capa de lógica intermedia para operaciones de equipos.
 * Actúa como puente entre los controladores y el servidor socket.
 */
public class EquipoLogica {
    private final String host;
    private final int port;

    public EquipoLogica() {
        this.host = SocketConfig.getHost();
        this.port = SocketConfig.getPort();
    }

    public EquipoLogica(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Carga la lista de equipos activos desde el servidor.
     */
    public List<EquipoDTO> cargarEquipos() throws Exception {
        try (EquipoServiceClientSocket service = new EquipoServiceClientSocket(host, port)) {
            return service.listar();
        }
    }

    /**
     * Obtiene un equipo específico por su código.
     */
    public EquipoDTO obtenerEquipo(String codigo) throws Exception {
        try (EquipoServiceClientSocket service = new EquipoServiceClientSocket(host, port)) {
            return service.obtener(codigo);
        }
    }

    /**
     * Crea un nuevo equipo en el servidor.
     * @return ID del equipo creado
     */
    public int crearEquipo(EquipoDTO equipo) throws Exception {
        try (EquipoServiceClientSocket service = new EquipoServiceClientSocket(host, port)) {
            return service.crear(equipo);
        }
    }

    /**
     * Actualiza un equipo existente.
     */
    public void actualizarEquipo(EquipoDTO equipo) throws Exception {
        try (EquipoServiceClientSocket service = new EquipoServiceClientSocket(host, port)) {
            service.actualizar(equipo);
        }
    }

    /**
     * Elimina (desactiva) un equipo.
     */
    public void eliminarEquipo(String codigo) throws Exception {
        try (EquipoServiceClientSocket service = new EquipoServiceClientSocket(host, port)) {
            service.eliminar(codigo);
        }
    }
}

