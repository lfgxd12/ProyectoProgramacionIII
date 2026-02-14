package org.example.entregable2.logica;

import org.example.entregable2.client.JornadaServiceClientSocket;
import org.example.entregable2.dto.JornadaDTO;

import java.util.List;

/**
 * Capa de lógica de negocio para Jornadas.
 * Se comunica con el servidor mediante sockets.
 * NO accede directamente a la base de datos.
 */
public class JornadaLogica {

    private final String host = "127.0.0.1";
    private final int port = 5050;

    /**
     * Lista jornadas por temporada
     */
    public List<JornadaDTO> listarPorTemporada(int idTemporada) throws Exception {
        try (var service = new JornadaServiceClientSocket(host, port)) {
            return service.listarPorTemporada(idTemporada);
        }
    }

    /**
     * Obtiene una jornada por ID
     */
    public JornadaDTO obtenerPorId(int idJornada) throws Exception {
        try (var service = new JornadaServiceClientSocket(host, port)) {
            return service.obtenerPorId(idJornada);
        }
    }

    /**
     * Crea una nueva jornada
     * @return ID generado de la jornada
     */
    public int crearJornada(JornadaDTO jornada) throws Exception {
        try (var service = new JornadaServiceClientSocket(host, port)) {
            return service.crear(jornada);
        }
    }

    /**
     * Actualiza una jornada existente
     */
    public void actualizarJornada(JornadaDTO jornada) throws Exception {
        try (var service = new JornadaServiceClientSocket(host, port)) {
            service.actualizar(jornada);
        }
    }

    /**
     * Elimina una jornada
     */
    public void eliminarJornada(int idJornada) throws Exception {
        try (var service = new JornadaServiceClientSocket(host, port)) {
            service.eliminar(idJornada);
        }
    }
}

