package org.example.entregable2.servicios;

import javafx.concurrent.Task;
import org.example.entregable2.dto.TemporadaDTO;
import org.example.entregable2.logica.TemporadaLogica;

import java.util.List;
import java.util.function.Consumer;

/**
 * Servicio para ejecutar operaciones de Temporada en segundo plano
 * usando JavaFX Tasks para no bloquear la interfaz gráfica.
 */
public class TemporadaService {

    private final TemporadaLogica temporadaLogica = new TemporadaLogica();

    /**
     * Lista temporadas en segundo plano
     */
    public void listarTemporadasAsync(Consumer<List<TemporadaDTO>> onSuccess, Consumer<Throwable> onError) {
        Task<List<TemporadaDTO>> task = new Task<>() {
            @Override
            protected List<TemporadaDTO> call() throws Exception {
                return temporadaLogica.listarTemporadas();
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Obtiene la temporada activa en segundo plano
     */
    public void obtenerTemporadaActivaAsync(Consumer<TemporadaDTO> onSuccess, Consumer<Throwable> onError) {
        Task<TemporadaDTO> task = new Task<>() {
            @Override
            protected TemporadaDTO call() throws Exception {
                return temporadaLogica.obtenerTemporadaActiva();
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Crea una temporada en segundo plano
     */
    public void crearTemporadaAsync(TemporadaDTO temporada, Consumer<Integer> onSuccess, Consumer<Throwable> onError) {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return temporadaLogica.crearTemporada(temporada);
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Actualiza una temporada en segundo plano
     */
    public void actualizarTemporadaAsync(TemporadaDTO temporada, Runnable onSuccess, Consumer<Throwable> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                temporadaLogica.actualizarTemporada(temporada);
                return null;
            }
        };

        task.setOnSucceeded(event -> onSuccess.run());
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Elimina una temporada en segundo plano
     */
    public void eliminarTemporadaAsync(int idTemporada, Runnable onSuccess, Consumer<Throwable> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                temporadaLogica.eliminarTemporada(idTemporada);
                return null;
            }
        };

        task.setOnSucceeded(event -> onSuccess.run());
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}

