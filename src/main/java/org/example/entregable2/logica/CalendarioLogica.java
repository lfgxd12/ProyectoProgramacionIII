package org.example.entregable2.logica;

import org.example.entregable2.client.CalendarioServiceClientSocket;
import org.example.entregable2.client.SocketConfig;
import org.example.entregable2.dto.PartidoDTO;

import java.util.List;


public class CalendarioLogica {
    private final String host;
    private final int port;

    public CalendarioLogica() {
        this.host = SocketConfig.getHost();
        this.port = SocketConfig.getPort();
    }

    public CalendarioLogica(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public String generarCalendario(boolean idaVuelta) throws Exception {
        try (CalendarioServiceClientSocket service = new CalendarioServiceClientSocket(host, port)) {
            return service.generar(idaVuelta);
        }
    }


    public List<PartidoDTO> obtenerCalendario() throws Exception {
        try (CalendarioServiceClientSocket service = new CalendarioServiceClientSocket(host, port)) {
            return service.obtener();
        }
    }
}

