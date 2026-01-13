package org.example.entregable2.servicios;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.example.entregable2.logica.Liga;

import java.io.IOException;

public class NavigationService {

    private static NavigationService instance;
    private Stage primaryStage;
    private Liga liga;

    private NavigationService() {
    }

    public static NavigationService getInstance() {
        if (instance == null) {
            instance = new NavigationService();
        }
        return instance;
    }

    public void initialize(Stage stage, Liga liga) {
        this.primaryStage = stage;
        this.liga = liga;
    }

    public void navigateTo(String fxmlFile, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/entregable2/" + fxmlFile));
        Parent root = loader.load();

        Object controller = loader.getController();
        injectLiga(controller);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.show();
    }

    private void injectLiga(Object controller) {
        try {
            controller.getClass().getMethod("setLiga", Liga.class).invoke(controller, liga);
        } catch (Exception e) {
        }
    }

    public void mostrarMenuPrincipal() {
        try {
            navigateTo("menu.fxml", "Menú Principal - Premier League");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mostrarFormularioEquipo() {
        try {
            navigateTo("registrar-equipo.fxml", "Registrar Equipo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mostrarCalendario(int jornada) {
        try {
            navigateTo("calendario-partidos.fxml", "Calendario de Partidos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void solicitarResultado() {
        try {
            navigateTo("registrar-resultado.fxml", "Registrar Resultado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mostrarTablaPosiciones() {
        try {
            navigateTo("tabla-posiciones.fxml", "Tabla de Posiciones");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mostrarGenerarCalendario() {
        try {
            navigateTo("generar-calendario.fxml", "Generar Calendario");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mostrarListaEquipos() {
        try {
            navigateTo("lista-equipos.fxml", "Lista de Equipos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mostrarGestionEquipos() {
        try {
            navigateTo("gestion-de-equipos.fxml", "Gestión de Equipos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Liga getLiga() {
        return liga;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }
}

