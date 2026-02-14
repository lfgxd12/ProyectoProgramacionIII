package org.example.entregable2.servicios;

import javafx.concurrent.Task;
import org.example.entregable2.dto.JornadaDTO;
import org.example.entregable2.logica.JornadaLogica;

import java.util.List;
import java.util.function.Consumer;

/**
 * Servicio para ejecutar operaciones de Jornada en segundo plano
 * usando JavaFX Tasks para no bloquear la interfaz gráfica.
 */
public class JornadaService {

    private final JornadaLogica jornadaLogica = new JornadaLogica();

    /**
     * Lista jornadas por temporada en segundo plano
     */
    public void listarPorTemporadaAsync(int idTemporada, Consumer<List<JornadaDTO>> onSuccess, Consumer<Throwable> onError) {
        Task<List<JornadaDTO>> task = new Task<>() {
            @Override
            protected List<JornadaDTO> call() throws Exception {
                return jornadaLogica.listarPorTemporada(idTemporada);
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Obtiene una jornada por ID en segundo plano
     */
    public void obtenerPorIdAsync(int idJornada, Consumer<JornadaDTO> onSuccess, Consumer<Throwable> onError) {
        Task<JornadaDTO> task = new Task<>() {
            @Override
            protected JornadaDTO call() throws Exception {
                return jornadaLogica.obtenerPorId(idJornada);
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Crea una jornada en segundo plano
     */
    public void crearJornadaAsync(JornadaDTO jornada, Consumer<Integer> onSuccess, Consumer<Throwable> onError) {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return jornadaLogica.crearJornada(jornada);
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Actualiza una jornada en segundo plano
     */
    public void actualizarJornadaAsync(JornadaDTO jornada, Runnable onSuccess, Consumer<Throwable> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                jornadaLogica.actualizarJornada(jornada);
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
     * Elimina una jornada en segundo plano
     */
    public void eliminarJornadaAsync(int idJornada, Runnable onSuccess, Consumer<Throwable> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                jornadaLogica.eliminarJornada(idJornada);
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

