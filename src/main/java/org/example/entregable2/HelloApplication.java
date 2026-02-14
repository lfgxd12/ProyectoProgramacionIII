package org.example.entregable2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.servicios.NavigationService;
import org.example.entregable2.servicios.PersistenciaService;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Liga liga = null;
        PersistenciaService persistenciaService = PersistenciaService.getInstance();

        try {
            if (persistenciaService.existenDatos()) {
                liga = persistenciaService.cargarLiga("Premier League");
            } else {
                liga = new Liga("Premier League");
                System.out.println("Se creó una nueva liga");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
            liga = new Liga("Premier League");
        }

        NavigationService navigationService = NavigationService.getInstance();
        navigationService.initialize(stage, liga);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("portada.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Premier League - Sistema de Gestión");
        stage.setScene(scene);

        final Liga ligaFinal = liga;
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
