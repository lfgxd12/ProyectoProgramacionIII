package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entregable2.logica.Equipo;
import org.example.entregable2.logica.Liga;

import java.util.List;

public class ListaEquiposController {

    @FXML
    private TableView<Equipo> tablaEquipos;

    @FXML
    private TableColumn<Equipo, String> colCodigo;

    @FXML
    private TableColumn<Equipo, String> colNombre;

    @FXML
    private TableColumn<Equipo, String> colCiudad;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnVolver;

    private Liga liga;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarEventos();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
    }

    private void configurarEventos() {
        if (btnActualizar != null) {
            btnActualizar.setOnAction(event -> onActualizarClick());
        }
        if (btnVolver != null) {
            btnVolver.setOnAction(event -> onVolverClick());
        }
    }

    public void cargarEquipos() {
        if (liga != null) {
            List<Equipo> equipos = liga.getEquipos();
            ObservableList<Equipo> equiposObservable = FXCollections.observableArrayList(equipos);
            tablaEquipos.setItems(equiposObservable);
        }
    }

    @FXML
    public void onActualizarClick() {
        cargarEquipos();
    }

    @FXML
    public void onVolverClick() {
        org.example.entregable2.servicios.NavigationService.getInstance().mostrarMenuPrincipal();
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
        cargarEquipos();
    }
}

