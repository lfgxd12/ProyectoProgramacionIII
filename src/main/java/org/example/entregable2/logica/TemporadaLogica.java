package org.example.entregable2.logica;

import org.example.entregable2.client.TemporadaServiceClientSocket;
import org.example.entregable2.dto.TemporadaDTO;

import java.util.List;

/**
 * Capa de lógica de negocio para Temporadas.
 * Se comunica con el servidor mediante sockets.
 * NO accede directamente a la base de datos.
 */
public class TemporadaLogica {

    private final String host = "127.0.0.1";
    private final int port = 5050;

    /**
     * Lista todas las temporadas
     */
    public List<TemporadaDTO> listarTemporadas() throws Exception {
        try (var service = new TemporadaServiceClientSocket(host, port)) {
            return service.listar();
        }
    }

    /**
     * Obtiene la temporada activa
     */
    public TemporadaDTO obtenerTemporadaActiva() throws Exception {
        try (var service = new TemporadaServiceClientSocket(host, port)) {
            return service.obtenerActiva();
        }
    }

    /**
     * Crea una nueva temporada
     * @return ID generado de la temporada
     */
    public int crearTemporada(TemporadaDTO temporada) throws Exception {
        try (var service = new TemporadaServiceClientSocket(host, port)) {
            return service.crear(temporada);
        }
    }

    /**
     * Actualiza una temporada existente
     */
    public void actualizarTemporada(TemporadaDTO temporada) throws Exception {
        try (var service = new TemporadaServiceClientSocket(host, port)) {
            service.actualizar(temporada);
        }
    }

    /**
     * Elimina (desactiva) una temporada
     */
    public void eliminarTemporada(int idTemporada) throws Exception {
        try (var service = new TemporadaServiceClientSocket(host, port)) {
            service.eliminar(idTemporada);
        }
    }
}

