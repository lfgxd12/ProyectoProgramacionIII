package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.servicios.NavigationService;

public class MenuController {


    @FXML
    private Button btnListaEquipos;

    @FXML
    private Button btnGenerarCalentario;

    @FXML
    private Button btbListarCalendarioPorJornada;

    @FXML
    private Button btnRegistrarResultados;

    @FXML
    private Button btbMostrarTablaDePosiciones;

    @FXML
    private Button btnSimularJornada;

    @FXML
    private Button btnSimulacion;

    @FXML
    private Button btnSalir2;

    private Liga liga;

    @FXML
    public void initialize() {
        configurarEventos();
    }

    private void configurarEventos() {
        btnListaEquipos.setOnAction(event -> onListaEquiposClick());
        btnGenerarCalentario.setOnAction(event -> onGenerarCalendarioClick());
        btbListarCalendarioPorJornada.setOnAction(event -> onListarCalendarioClick());
        btnRegistrarResultados.setOnAction(event -> onRegistrarResultadosClick());
        btbMostrarTablaDePosiciones.setOnAction(event -> onMostrarTablaClick());
        if (btnSimularJornada != null) {
            btnSimularJornada.setOnAction(event -> onSimularJornadaClick());
        }
        if (btnSimulacion != null) {
            btnSimulacion.setOnAction(event -> onSimulacionClick());
        }
        btnSalir2.setOnAction(event -> onSalirClick());
    }


    @FXML
    public void onListaEquiposClick() {
        NavigationService.getInstance().mostrarListaEquipos();
    }

    @FXML
    public void onGenerarCalendarioClick() {
        NavigationService.getInstance().mostrarGenerarCalendario();
    }

    @FXML
    public void onListarCalendarioClick() {
        NavigationService.getInstance().mostrarCalendario(0);
    }

    @FXML
    public void onRegistrarResultadosClick() {
        NavigationService.getInstance().solicitarResultado();
    }

    @FXML
    public void onMostrarTablaClick() {
        NavigationService.getInstance().mostrarTablaPosiciones();
    }

    @FXML
    public void onSimularJornadaClick() {
        NavigationService.getInstance().mostrarSimulacion();
    }

    @FXML
    public void onSimulacionClick() {
        NavigationService.getInstance().mostrarSimulacion();
    }

    @FXML
    public void onSalirClick() {
        System.exit(0);
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public Liga getLiga() {
        return liga;
    }
}
