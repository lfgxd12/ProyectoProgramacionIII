package org.example.entregable2;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.entregable2.dto.EquipoDTO;
import org.example.entregable2.logica.Equipo;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.logica.EquipoLogica;
import org.example.entregable2.servicios.NavigationService;
import org.example.entregable2.servicios.PersistenciaService;

import java.io.IOException;
import java.util.List;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Liga liga = new Liga("Premier League");
        PersistenciaService persistenciaService = PersistenciaService.getInstance();

        try {
            if (persistenciaService.existenDatos()) {
                Liga ligaLocal = persistenciaService.cargarLiga("Premier League");
                System.out.println("Datos locales disponibles como respaldo");
                for (Equipo eq : ligaLocal.getEquipos()) {
                    liga.registrarEquipo(eq);
                }
            }
        } catch (Exception e) {
            System.out.println("No hay datos locales, se iniciará con liga vacía");
        }

        NavigationService navigationService = NavigationService.getInstance();
        navigationService.initialize(stage, liga);

        final Liga ligaFinal = liga;
        Task<List<EquipoDTO>> cargarDesdeServidor = new Task<>() {
            @Override
            protected List<EquipoDTO> call() throws Exception {
                EquipoLogica equipoLogica = new EquipoLogica();
                return equipoLogica.cargarEquipos();
            }
        };

        cargarDesdeServidor.setOnSucceeded(event -> {
            try {
                List<EquipoDTO> equiposServidor = cargarDesdeServidor.getValue();
                System.out.println("Equipos cargados desde el servidor: " + equiposServidor.size());

                for (EquipoDTO dto : equiposServidor) {
                    try {
                        Equipo eq = new Equipo(dto.getNombre(), dto.getCiudad(), dto.getCodigo());
                        eq.setEstadio(dto.getEstadio());
                        eq.setAnnioFundacion(String.valueOf(dto.getAnioFundacion()));

                        boolean existe = ligaFinal.getEquipos().stream()
                            .anyMatch(e -> e.getCodigo().equals(dto.getCodigo()));

                        if (!existe) {
                            ligaFinal.registrarEquipo(eq);
                        }
                    } catch (Exception e) {
                        System.err.println("Error al agregar equipo: " + e.getMessage());
                    }
                }
                System.out.println("Sincronización con servidor completada");
            } catch (Exception e) {
                System.err.println("Error al procesar equipos del servidor: " + e.getMessage());
            }
        });

        cargarDesdeServidor.setOnFailed(event -> {
            System.out.println("No se pudo conectar al servidor, trabajando en modo local");
            System.out.println("Equipos disponibles localmente: " + ligaFinal.getEquipos().size());
        });

        new Thread(cargarDesdeServidor).start();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("portada.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Premier League - Sistema de Gestión");
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            try {
                persistenciaService.guardarLiga(ligaFinal);
                System.out.println("Datos guardados correctamente");
            } catch (Exception e) {
                System.err.println("Error al guardar datos: " + e.getMessage());
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
