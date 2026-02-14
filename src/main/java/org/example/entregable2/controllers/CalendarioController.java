package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entregable2.dto.PartidoDTO;
import org.example.entregable2.dto.EquipoDTO;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.servicios.PartidoService;
import org.example.entregable2.servicios.EquipoService;
import org.example.entregable2.servicios.TemporadaService;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class CalendarioController {

    @FXML
    private ComboBox<Integer> comboJornadas;

    @FXML
    private TableView<PartidoDTO> tablaPartidos;

    @FXML
    private TableColumn<PartidoDTO, Integer> colId;

    @FXML
    private TableColumn<PartidoDTO, Integer> colJornada;

    @FXML
    private TableColumn<PartidoDTO, String> colLocal;

    @FXML
    private TableColumn<PartidoDTO, String> colVisitante;

    @FXML
    private TableColumn<PartidoDTO, String> colResultado;

    @FXML
    private Button btnFiltrar;

    @FXML
    private Button btnMostrarTodos;

    @FXML
    private Button btnVolver;

    private Liga liga;
    private final PartidoService partidoService = new PartidoService();
    private final TemporadaService temporadaService = new TemporadaService();
    private int idTemporadaActual = -1;
    private Map<String, String> nombresEquipos = new HashMap<>();

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarEventos();
        cargarTemporadaActiva();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colJornada.setCellValueFactory(cellData -> cellData.getValue().jornadaProperty().asObject());

        colLocal.setCellValueFactory(cellData -> {
            String codigo = cellData.getValue().getEquipoLocalCodigo();
            return new javafx.beans.property.SimpleStringProperty(
                nombresEquipos.getOrDefault(codigo, codigo));
        });

        colVisitante.setCellValueFactory(cellData -> {
            String codigo = cellData.getValue().getEquipoVisitanteCodigo();
            return new javafx.beans.property.SimpleStringProperty(
                nombresEquipos.getOrDefault(codigo, codigo));
        });

        colResultado.setCellValueFactory(cellData -> {
            PartidoDTO partido = cellData.getValue();
            String resultado;

            if ("FINALIZADO".equals(partido.getEstado())) {
                resultado = partido.getGolesLocal() + " - " + partido.getGolesVisitante();
            } else if ("CANCELADO".equals(partido.getEstado())) {
                resultado = "Cancelado";
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

    private void cargarTemporadaActiva() {
        temporadaService.obtenerTemporadaActivaAsync(
            temporada -> {
                if (temporada != null) {
                    idTemporadaActual = temporada.getIdTemporada();
                    cargarEquiposParaNombres();
                    cargarJornadas();
                }
            },
            error -> {
                mostrarAlerta(AlertType.WARNING, "Advertencia",
                    "No se pudo cargar la temporada activa: " + error.getMessage());
            }
        );
    }

    private void cargarEquiposParaNombres() {
        EquipoService equipoService = new EquipoService();
        equipoService.listarEquiposAsync(
            equipos -> {
                for (EquipoDTO equipo : equipos) {
                    nombresEquipos.put(equipo.getCodigo(), equipo.getNombre());
                }
            },
            error -> {
                System.err.println("Error cargando equipos: " + error.getMessage());
            }
        );
    }

    private void cargarJornadas() {
        if (idTemporadaActual > 0) {
            partidoService.listarPorTemporadaAsync(
                idTemporadaActual,
                partidos -> {
                    // Extraer jornadas únicas
                    List<Integer> jornadas = partidos.stream()
                        .map(PartidoDTO::getJornada)
                        .distinct()
                        .sorted()
                        .toList();

                    ObservableList<Integer> jornadasObservable = FXCollections.observableArrayList(jornadas);
                    comboJornadas.setItems(jornadasObservable);
                },
                error -> {
                    mostrarAlerta(AlertType.ERROR, "Error",
                        "Error al cargar jornadas: " + error.getMessage());
                }
            );
        }
    }

    @FXML
    public void onFiltrarClick() {
        Integer jornadaSeleccionada = comboJornadas.getValue();
        if (jornadaSeleccionada != null && idTemporadaActual > 0) {
            btnFiltrar.setDisable(true);

            partidoService.listarPorJornadaAsync(
                idTemporadaActual,
                jornadaSeleccionada,
                partidos -> {
                    btnFiltrar.setDisable(false);
                    ObservableList<PartidoDTO> partidosObservable = FXCollections.observableArrayList(partidos);
                    tablaPartidos.setItems(partidosObservable);
                },
                error -> {
                    btnFiltrar.setDisable(false);
                    mostrarAlerta(AlertType.ERROR, "Error",
                        "Error al filtrar partidos: " + error.getMessage());
                }
            );
        }
    }

    @FXML
    public void onMostrarTodosClick() {
        if (idTemporadaActual > 0) {
            btnMostrarTodos.setDisable(true);

            partidoService.listarPorTemporadaAsync(
                idTemporadaActual,
                partidos -> {
                    btnMostrarTodos.setDisable(false);
                    ObservableList<PartidoDTO> partidosObservable = FXCollections.observableArrayList(partidos);
                    tablaPartidos.setItems(partidosObservable);
                },
                error -> {
                    btnMostrarTodos.setDisable(false);
                    mostrarAlerta(AlertType.ERROR, "Error",
                        "Error al cargar partidos: " + error.getMessage());
                }
            );
        }
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
        cargarTemporadaActiva();
    }
}
