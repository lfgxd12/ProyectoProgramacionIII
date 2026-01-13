package org.example.entregable2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.servicios.NavigationService;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Liga liga = new Liga("Premier League");

        NavigationService navigationService = NavigationService.getInstance();
        navigationService.initialize(stage, liga);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("portada.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Premier League - Sistema de Gestión");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

