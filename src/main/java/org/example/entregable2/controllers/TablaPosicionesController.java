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

public class TablaPosicionesController {

    @FXML
    private TableView<Equipo> tablaPosiciones;

    @FXML
    private TableColumn<Equipo, Integer> colPosicion;

    @FXML
    private TableColumn<Equipo, String> colEquipo;

    @FXML
    private TableColumn<Equipo, Integer> colPJ;

    @FXML
    private TableColumn<Equipo, Integer> colG;

    @FXML
    private TableColumn<Equipo, Integer> colE;

    @FXML
    private TableColumn<Equipo, Integer> colP;

    @FXML
    private TableColumn<Equipo, Integer> colGF;

    @FXML
    private TableColumn<Equipo, Integer> colGC;

    @FXML
    private TableColumn<Equipo, Integer> colDG;

    @FXML
    private TableColumn<Equipo, Integer> colPTS;

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
        colEquipo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPJ.setCellValueFactory(new PropertyValueFactory<>("pj"));
        colG.setCellValueFactory(new PropertyValueFactory<>("g"));
        colE.setCellValueFactory(new PropertyValueFactory<>("e"));
        colP.setCellValueFactory(new PropertyValueFactory<>("p"));
        colGF.setCellValueFactory(new PropertyValueFactory<>("gf"));
        colGC.setCellValueFactory(new PropertyValueFactory<>("gc"));
        colDG.setCellValueFactory(new PropertyValueFactory<>("dg"));
        colPTS.setCellValueFactory(new PropertyValueFactory<>("pts"));

        colPosicion.setCellFactory(col -> new javafx.scene.control.TableCell<Equipo, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
    }

    private void configurarEventos() {
        if (btnActualizar != null) {
            btnActualizar.setOnAction(event -> onActualizarClick());
        }
        if (btnVolver != null) {
            btnVolver.setOnAction(event -> onVolverClick());
        }
    }

    public void cargarTablaPosiciones() {
        if (liga != null) {
            List<Equipo> tabla = liga.calcularTabla();
            ObservableList<Equipo> equiposObservable = FXCollections.observableArrayList(tabla);
            tablaPosiciones.setItems(equiposObservable);
        }
    }

    @FXML
    public void onActualizarClick() {
        cargarTablaPosiciones();
    }

    @FXML
    public void onVolverClick() {
        org.example.entregable2.servicios.NavigationService.getInstance().mostrarMenuPrincipal();
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
        cargarTablaPosiciones();
    }
}

