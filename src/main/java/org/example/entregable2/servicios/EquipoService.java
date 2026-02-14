package org.example.entregable2.servicios;

import javafx.concurrent.Task;
import org.example.entregable2.dto.EquipoDTO;
import org.example.entregable2.logica.EquipoLogica;

import java.util.List;
import java.util.function.Consumer;


public class EquipoService {

    private final EquipoLogica equipoLogica = new EquipoLogica();

    public void listarEquiposAsync(Consumer<List<EquipoDTO>> onSuccess, Consumer<Throwable> onError) {
        Task<List<EquipoDTO>> task = new Task<>() {
            @Override
            protected List<EquipoDTO> call() throws Exception {
                return equipoLogica.cargarEquipos();
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


    public void crearEquipoAsync(EquipoDTO equipo, Consumer<Integer> onSuccess, Consumer<Throwable> onError) {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return equipoLogica.crearEquipo(equipo);
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @SuppressWarnings("unused")
    public void actualizarEquipoAsync(EquipoDTO equipo, Runnable onSuccess, Consumer<Throwable> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                equipoLogica.actualizarEquipo(equipo);
                return null;
            }
        };

        task.setOnSucceeded(event -> onSuccess.run());
        task.setOnFailed(event -> onError.accept(task.getException()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @SuppressWarnings("unused")
    public void eliminarEquipoAsync(int idEquipo, Runnable onSuccess, Consumer<Throwable> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                equipoLogica.eliminarEquipo(idEquipo);
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

