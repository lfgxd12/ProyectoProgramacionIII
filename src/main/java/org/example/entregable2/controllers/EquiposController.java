package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entregable2.logica.Equipo;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.servicios.NavigationService;

import java.util.List;

public class EquiposController {

    @FXML
    private Button btnRegistrarEquipo;

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
    private Button btnModificar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnVolver;

    private Liga liga;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarEventos();
    }

    private void configurarColumnas() {
        btnRegistrarEquipo.setOnAction(event -> onRegistrarEquipoClick());
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
    }

    @FXML
    public void onRegistrarEquipoClick() {
        NavigationService.getInstance().mostrarFormularioEquipo();
    }

    private void configurarEventos() {
        if (btnActualizar != null) {
            btnActualizar.setOnAction(event -> onActualizarClick());
        }

        if (btnModificar != null) {
            btnModificar.setOnAction(event -> onModificarClick());
        }

        if (btnEliminar != null) {
            btnEliminar.setOnAction(event -> onEliminarClick());
        }

        if (btnVolver != null) {
            btnVolver.setOnAction(event -> onVolverClick());
        }
    }

    @FXML
    public void onModificarClick(){
        Equipo equipoSeleccionado = tablaEquipos.getSelectionModel().getSelectedItem();

        if (equipoSeleccionado == null){
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                    "Debe seleccionar un equipo de la tabla para modificar");
            return;
        }

        javafx.scene.control.Dialog<javafx.util.Pair<String, String>> dialog =
            new javafx.scene.control.Dialog<>();
        dialog.setTitle("Modificar Equipo");
        dialog.setHeaderText("Editar datos del equipo: " + equipoSeleccionado.getNombre());

        javafx.scene.control.ButtonType guardarBtn =
            new javafx.scene.control.ButtonType("Guardar",
                javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(
            guardarBtn, javafx.scene.control.ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        javafx.scene.control.TextField txtNombre = new javafx.scene.control.TextField();
        txtNombre.setText(equipoSeleccionado.getNombre());
        javafx.scene.control.TextField txtCiudad = new javafx.scene.control.TextField();
        txtCiudad.setText(equipoSeleccionado.getCiudad());

        grid.add(new javafx.scene.control.Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new javafx.scene.control.Label("Ciudad:"), 0, 1);
        grid.add(txtCiudad, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == guardarBtn) {
                return new javafx.util.Pair<>(txtNombre.getText(), txtCiudad.getText());
            }
            return null;
        });

        java.util.Optional<javafx.util.Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(datos -> {
            try {
                if (datos.getKey().trim().isEmpty() || datos.getValue().trim().isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                        "Todos los campos son obligatorios");
                    return;
                }

                liga.editarEquipo(equipoSeleccionado.getCodigo(),
                    datos.getKey().trim(), datos.getValue().trim());

                try {
                    org.example.entregable2.servicios.PersistenciaService.getInstance().guardarLiga(liga);
                } catch (Exception ex) {
                    System.err.println("Error al guardar datos: " + ex.getMessage());
                }

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Equipo modificado correctamente");
                cargarEquipos();

            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        });
    }

    @FXML
    public void onEliminarClick(){
        Equipo equipoSeleccionado = tablaEquipos.getSelectionModel().getSelectedItem();

        if (equipoSeleccionado == null){
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                    "Debe seleccionar un equipo de la tabla para eliminar");
            return;
        }

        Alert confimacion = new Alert(Alert.AlertType.CONFIRMATION);
        confimacion.setTitle("Confirmar Eliminación");
        confimacion.setHeaderText("¿Está seguro de que desea eliminar el equipo seleccionado?");
        confimacion.setContentText(
                "Equipo: " + equipoSeleccionado.getNombre() + "\n" +
                "Código: " + equipoSeleccionado.getCodigo() + "\n" +
                "Ciudad: " + equipoSeleccionado.getCiudad() + "\n\n" +
                "NOTA: El equipo será marcado como eliminado.\n" +
                "Sus partidos pendientes aparecerán como cancelados.\n" +
                "Los partidos ya jugados se mantendrán en el historial."
        );

        java.util.Optional<javafx.scene.control.ButtonType> resultado =
                confimacion.showAndWait();

        if (resultado.isPresent() &&
            resultado.get() == javafx.scene.control.ButtonType.OK) {
            try{
                boolean eliminado = liga.eliminarEquipo(equipoSeleccionado.getCodigo());

                if(eliminado){
                    try {
                        org.example.entregable2.servicios.PersistenciaService.getInstance().guardarLiga(liga);
                    } catch (Exception ex) {
                        System.err.println("Error al guardar datos: " + ex.getMessage());
                    }

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "Equipo marcado como eliminado correctamente.\n" +
                            "Ya no aparecerá en la lista de equipos activos.");
                    cargarEquipos();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo eliminar el equipo");
                }
            } catch (Exception e ){
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "Error al eliminar: " + e.getMessage());
            }
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}

