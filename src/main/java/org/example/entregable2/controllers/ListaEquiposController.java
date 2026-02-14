package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entregable2.dto.EquipoDTO;
import org.example.entregable2.logica.Equipo;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.servicios.EquipoService;

import java.util.List;
import java.util.stream.Collectors;

public class ListaEquiposController {

    @FXML
    private TableView<EquipoDTO> tablaEquipos;

    @FXML
    private TableColumn<EquipoDTO, String> colCodigo;

    @FXML
    private TableColumn<EquipoDTO, String> colNombre;

    @FXML
    private TableColumn<EquipoDTO, String> colCiudad;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnVolver;

    private Liga liga;
    private final EquipoService equipoService = new EquipoService();

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarEventos();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colCiudad.setCellValueFactory(cellData -> cellData.getValue().ciudadProperty());
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
        btnActualizar.setDisable(true);

        equipoService.listarEquiposAsync(
            equipos -> {
                btnActualizar.setDisable(false);

                // Actualizar liga si existe
                if (liga != null) {
                    liga.getEquipos().clear();
                    List<Equipo> equiposLista = equipos.stream()
                        .map(Equipo::fromDTO)
                        .collect(Collectors.toList());
                    liga.getEquipos().addAll(equiposLista);
                }

                ObservableList<EquipoDTO> equiposObservable = FXCollections.observableArrayList(equipos);
                tablaEquipos.setItems(equiposObservable);
            },
            error -> {
                btnActualizar.setDisable(false);
                mostrarAlerta(AlertType.ERROR, "Error",
                    "Error al cargar equipos: " + error.getMessage());
            }
        );
    }

    @FXML
    public void onActualizarClick() {
        cargarEquipos();
    }

    @FXML
    public void onVolverClick() {
        org.example.entregable2.servicios.NavigationService.getInstance().mostrarMenuPrincipal();
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
        cargarEquipos();
    }
}
