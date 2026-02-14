package org.example.entregable2.servicios;

import javafx.concurrent.Task;
import org.example.entregable2.dto.PartidoDTO;
import org.example.entregable2.logica.PartidoLogica;

import java.util.List;
import java.util.function.Consumer;

/**
 * Servicio para ejecutar operaciones de Partido en segundo plano
 * usando JavaFX Tasks para no bloquear la interfaz gráfica.
 */
public class PartidoService {

    private final PartidoLogica partidoLogica = new PartidoLogica();

    /**
     * Lista partidos por jornada en segundo plano
     */
    public void listarPorJornadaAsync(int idTemporada, int numeroJornada, Consumer<List<PartidoDTO>> onSuccess, Consumer<Throwable> onError) {
        Task<List<PartidoDTO>> task = new Task<>() {
            @Override
            protected List<PartidoDTO> call() throws Exception {
                return partidoLogica.listarPorJornada(idTemporada, numeroJornada);
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Lista partidos por temporada en segundo plano
     */
    public void listarPorTemporadaAsync(int idTemporada, Consumer<List<PartidoDTO>> onSuccess, Consumer<Throwable> onError) {
        Task<List<PartidoDTO>> task = new Task<>() {
            @Override
            protected List<PartidoDTO> call() throws Exception {
                return partidoLogica.listarPorTemporada(idTemporada);
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Crea un partido en segundo plano
     */
    public void crearPartidoAsync(PartidoDTO partido, Consumer<Integer> onSuccess, Consumer<Throwable> onError) {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return partidoLogica.crearPartido(partido);
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Registra resultado de un partido en segundo plano
     */
    public void registrarResultadoAsync(int idPartido, int golesLocal, int golesVisitante, Runnable onSuccess, Consumer<Throwable> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                partidoLogica.registrarResultado(idPartido, golesLocal, golesVisitante);
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
     * Actualiza un partido en segundo plano
     */
    public void actualizarPartidoAsync(PartidoDTO partido, Runnable onSuccess, Consumer<Throwable> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                partidoLogica.actualizarPartido(partido);
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
     * Elimina un partido en segundo plano
     */
    public void eliminarPartidoAsync(int idPartido, Runnable onSuccess, Consumer<Throwable> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                partidoLogica.eliminarPartido(idPartido);
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

