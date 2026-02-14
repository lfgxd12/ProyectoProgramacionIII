package org.example.entregable2.logica;

import org.example.entregable2.client.PartidoServiceClientSocket;
import org.example.entregable2.dto.PartidoDTO;

import java.util.List;

/**
 * Capa de lógica de negocio para Partidos.
 * Se comunica con el servidor mediante sockets.
 * NO accede directamente a la base de datos.
 */
public class PartidoLogica {

    private final String host = "127.0.0.1";
    private final int port = 5050;

    /**
     * Lista partidos por jornada
     */
    public List<PartidoDTO> listarPorJornada(int idTemporada, int numeroJornada) throws Exception {
        try (var service = new PartidoServiceClientSocket(host, port)) {
            return service.listarPorJornada(idTemporada);
        }
    }

    /**
     * Lista partidos por temporada
     */
    public List<PartidoDTO> listarPorTemporada(int idTemporada) throws Exception {
        try (var service = new PartidoServiceClientSocket(host, port)) {
            return service.listarPorTemporada(idTemporada);
        }
    }

    /**
     * Crea un nuevo partido
     * @return ID generado del partido
     */
    public int crearPartido(PartidoDTO partido) throws Exception {
        try (var service = new PartidoServiceClientSocket(host, port)) {
            return service.crear(partido);
        }
    }

    /**
     * Registra el resultado de un partido
     */
    public void registrarResultado(int idPartido, int golesLocal, int golesVisitante) throws Exception {
        try (var service = new PartidoServiceClientSocket(host, port)) {
            service.registrarResultado(idPartido, golesLocal, golesVisitante);
        }
    }

    /**
     * Actualiza un partido existente
     */
    public void actualizarPartido(PartidoDTO partido) throws Exception {
        try (var service = new PartidoServiceClientSocket(host, port)) {
            service.actualizar(partido);
        }
    }

    /**
     * Elimina un partido
     */
    public void eliminarPartido(int idPartido) throws Exception {
        try (var service = new PartidoServiceClientSocket(host, port)) {
            service.eliminar(idPartido);
        }
    }
}

