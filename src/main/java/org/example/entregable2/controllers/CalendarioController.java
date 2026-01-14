package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.logica.Partido;

import java.util.List;
import java.util.Map;

public class CalendarioController {

    @FXML
    private ComboBox<Integer> comboJornadas;

    @FXML
    private TableView<Partido> tablaPartidos;

    @FXML
    private TableColumn<Partido, Integer> colId;

    @FXML
    private TableColumn<Partido, Integer> colJornada;

    @FXML
    private TableColumn<Partido, String> colLocal;

    @FXML
    private TableColumn<Partido, String> colVisitante;

    @FXML
    private TableColumn<Partido, String> colResultado;

    @FXML
    private Button btnFiltrar;

    @FXML
    private Button btnMostrarTodos;

    @FXML
    private Button btnVolver;

    private Liga liga;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarEventos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colJornada.setCellValueFactory(new PropertyValueFactory<>("jornada"));

        colLocal.setCellValueFactory(cellData -> {
            Partido partido = cellData.getValue();
            String nombreLocal = partido.getLocal().getNombre();
            if (partido.getLocal().isEliminado()) {
                nombreLocal += " (Equipo Borrado)";
            }
            return new javafx.beans.property.SimpleStringProperty(nombreLocal);
        });

        colVisitante.setCellValueFactory(cellData -> {
            Partido partido = cellData.getValue();
            String nombreVisitante = partido.getVisitante().getNombre();
            if (partido.getVisitante().isEliminado()) {
                nombreVisitante += " (Equipo Borrado)";
            }
            return new javafx.beans.property.SimpleStringProperty(nombreVisitante);
        });

        colResultado.setCellValueFactory(cellData -> {
            Partido partido = cellData.getValue();
            String resultado;

            // Si algún equipo fue eliminado y el partido no se jugó
            if (!partido.tieneResultado() &&
                (partido.getLocal().isEliminado() || partido.getVisitante().isEliminado())) {
                resultado = "Cancelado";
            } else if (partido.tieneResultado()) {
                resultado = partido.getGolesLocal() + " - " + partido.getGolesVisitante();
            } else {
                resultado = "Pendiente";
            }

            return new javafx.beans.property.SimpleStringProperty(resultado);
        });
    }

    private void configurarEventos() {
        if (btnFiltrar != null) {
            btnFiltrar.setOnAction(event -> onFiltrarClick());
        }
        if (btnMostrarTodos != null) {
            btnMostrarTodos.setOnAction(event -> onMostrarTodosClick());
        }
        if (btnVolver != null) {
            btnVolver.setOnAction(event -> onVolverClick());
        }
    }

    private void cargarJornadas() {
        if (liga != null) {
            Map<Integer, List<Partido>> partidosPorJornada = liga.getPartidosPorJornada();
            ObservableList<Integer> jornadas = FXCollections.observableArrayList(partidosPorJornada.keySet());
            comboJornadas.setItems(jornadas.sorted());
        }
    }

    @FXML
    public void onFiltrarClick() {
        Integer jornadaSeleccionada = comboJornadas.getValue();
        if (jornadaSeleccionada != null && liga != null) {
            List<Partido> partidos = liga.listarPartidosPorJornada(jornadaSeleccionada);
            ObservableList<Partido> partidosObservable = FXCollections.observableArrayList(partidos);
            tablaPartidos.setItems(partidosObservable);
        }
    }

    @FXML
    public void onMostrarTodosClick() {
        if (liga != null) {
            List<Partido> partidos = liga.getCalendario();
            ObservableList<Partido> partidosObservable = FXCollections.observableArrayList(partidos);
            tablaPartidos.setItems(partidosObservable);
        }
    }

    @FXML
    public void onVolverClick() {
        org.example.entregable2.servicios.NavigationService.getInstance().mostrarMenuPrincipal();
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
        cargarJornadas();
        onMostrarTodosClick();
    }
}

