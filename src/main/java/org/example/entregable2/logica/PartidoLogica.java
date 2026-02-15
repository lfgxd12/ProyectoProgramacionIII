package org.example.entregable2.logica;

import org.example.entregable2.client.PartidoServiceClientSocket;
import org.example.entregable2.client.SocketConfig;
import org.example.entregable2.dto.PartidoDTO;

import java.util.List;


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


    public List<PartidoDTO> cargarPartidos() throws Exception {
        try (PartidoServiceClientSocket service = new PartidoServiceClientSocket(host, port)) {
            return service.listar();
        }
    }


    public PartidoDTO obtenerPartido(int id) throws Exception {
        try (PartidoServiceClientSocket service = new PartidoServiceClientSocket(host, port)) {
            return service.obtener(id);
        }
    }


    public List<PartidoDTO> obtenerPartidosPorJornada(int numJornada) throws Exception {
        try (PartidoServiceClientSocket service = new PartidoServiceClientSocket(host, port)) {
            return service.obtenerPorJornada(numJornada);
        }
    }


    public void registrarResultado(int idPartido, int golesLocal, int golesVisitante) throws Exception {
        try (PartidoServiceClientSocket service = new PartidoServiceClientSocket(host, port)) {
            service.registrarResultado(idPartido, golesLocal, golesVisitante);
        }
    }
}

