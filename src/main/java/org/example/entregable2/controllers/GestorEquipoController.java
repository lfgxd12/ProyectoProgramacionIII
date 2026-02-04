package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.servicios.NavigationService;

public class GestorEquipoController {

    @FXML
    private Button btnInscribirEquipo;

    @FXML
    private Button btnListarEquipos;

    @FXML
    private Button btnVolver;

    private Liga liga;

    @FXML
    public void initialize() {
        configurarEventos();
    }

    private void configurarEventos() {
        if (btnInscribirEquipo != null) {
            btnInscribirEquipo.setOnAction(event -> onInscribirEquipoClick());
        }
        if (btnListarEquipos != null) {
            btnListarEquipos.setOnAction(event -> onListarEquiposClick());
        }
        if (btnVolver != null) {
            btnVolver.setOnAction(event -> onVolverClick());
        }
    }

    @FXML
    public void onInscribirEquipoClick() {
        NavigationService.getInstance().mostrarFormularioEquipo();
    }

    @FXML
    public void onListarEquiposClick() {
        NavigationService.getInstance().mostrarListaEquipos();
    }

    @FXML
    public void onVolverClick() {
        NavigationService.getInstance().mostrarMenuPrincipal();
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }
}
