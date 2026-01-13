package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.entregable2.servicios.NavigationService;

public class PortadaController {

    @FXML
    private Button btnIniciar;

    @FXML
    private Button btnSalir1;

    @FXML
    public void initialize() {
        configurarEventos();
    }

    private void configurarEventos() {
        btnIniciar.setOnAction(event -> onIniciarClick());
        btnSalir1.setOnAction(event -> onSalirClick());
    }

    @FXML
    public void onIniciarClick() {
        NavigationService.getInstance().mostrarMenuPrincipal();
    }

    @FXML
    public void onSalirClick() {
        Stage stage = (Stage) btnSalir1.getScene().getWindow();
        stage.close();
    }
}
