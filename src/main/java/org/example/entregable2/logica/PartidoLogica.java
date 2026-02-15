package org.example.entregable2.logica;

import org.example.entregable2.client.PartidoServiceClientSocket;
import org.example.entregable2.client.SocketConfig;
import org.example.entregable2.dto.PartidoDTO;

import java.util.List;

/**
 * Capa de lógica intermedia para operaciones de partidos.
 */
public class PartidoLogica {
    private final String host;
    private final int port;

    public PartidoLogica() {
        this.host = SocketConfig.getHost();
        this.port = SocketConfig.getPort();
    }

    public PartidoLogica(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Carga la lista de todos los partidos.
     */
    public List<PartidoDTO> cargarPartidos() throws Exception {
        try (PartidoServiceClientSocket service = new PartidoServiceClientSocket(host, port)) {
            return service.listar();
        }
    }

    /**
     * Obtiene un partido específico por su ID.
     */
    public PartidoDTO obtenerPartido(int id) throws Exception {
        try (PartidoServiceClientSocket service = new PartidoServiceClientSocket(host, port)) {
            return service.obtener(id);
        }
    }

    /**
     * Obtiene los partidos de una jornada específica.
     */
    public List<PartidoDTO> obtenerPartidosPorJornada(int numJornada) throws Exception {
        try (PartidoServiceClientSocket service = new PartidoServiceClientSocket(host, port)) {
            return service.obtenerPorJornada(numJornada);
        }
    }

    /**
     * Registra el resultado de un partido.
     */
    public void registrarResultado(int idPartido, int golesLocal, int golesVisitante) throws Exception {
        try (PartidoServiceClientSocket service = new PartidoServiceClientSocket(host, port)) {
            service.registrarResultado(idPartido, golesLocal, golesVisitante);
        }
    }
}

