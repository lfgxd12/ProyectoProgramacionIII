package org.example.entregable2.logica;

import org.example.entregable2.client.EquipoServiceClientSocket;
import org.example.entregable2.client.SocketConfig;
import org.example.entregable2.dto.EquipoDTO;

import java.util.List;


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


    public List<EquipoDTO> cargarEquipos() throws Exception {
        try (EquipoServiceClientSocket service = new EquipoServiceClientSocket(host, port)) {
            return service.listar();
        }
    }


    public EquipoDTO obtenerEquipo(String codigo) throws Exception {
        try (EquipoServiceClientSocket service = new EquipoServiceClientSocket(host, port)) {
            return service.obtener(codigo);
        }
    }


    public int crearEquipo(EquipoDTO equipo) throws Exception {
        try (EquipoServiceClientSocket service = new EquipoServiceClientSocket(host, port)) {
            return service.crear(equipo);
        }
    }


    public void actualizarEquipo(EquipoDTO equipo) throws Exception {
        try (EquipoServiceClientSocket service = new EquipoServiceClientSocket(host, port)) {
            service.actualizar(equipo);
        }
    }


    public void eliminarEquipo(String codigo) throws Exception {
        try (EquipoServiceClientSocket service = new EquipoServiceClientSocket(host, port)) {
            service.eliminar(codigo);
        }
    }
}

