package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import org.example.entregable2.dto.PartidoDTO;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.servicios.PartidoService;
import org.example.entregable2.servicios.TemporadaService;

import java.util.List;
import java.util.stream.Collectors;

public class RegistrarResultadosController {

    @FXML
    private ListView<Integer> tbJornada;

    @FXML
    private ListView<PartidoDTO> tbLocal;

    @FXML
    private ListView<PartidoDTO> tbVisita;

    @FXML
    private TextField txtGolesL;

    @FXML
    private TextField txtGolesV;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnVolver;

    private Liga liga;
    private PartidoDTO partidoSeleccionado;
    private final PartidoService partidoService = new PartidoService();
    private final TemporadaService temporadaService = new TemporadaService();
    private int idTemporadaActual = -1;

    @FXML
    public void initialize() {
        configurarEventos();
        cargarTemporadaActiva();
    }

    private void configurarEventos() {
        if (tbJornada != null) {
            tbJornada.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onJornadaSeleccionada());
        }
        if (tbLocal != null) {
            tbLocal.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onPartidoSeleccionado());

            // Personalizar cómo se muestran los partidos
            tbLocal.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(PartidoDTO partido, boolean empty) {
                    super.updateItem(partido, empty);
                    if (empty || partido == null) {
                        setText(null);
                    } else {
                        setText(partido.getEquipoLocalCodigo());
                    }
                }
            });
        }
        if (tbVisita != null) {
            tbVisita.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(PartidoDTO partido, boolean empty) {
                    super.updateItem(partido, empty);
                    if (empty || partido == null) {
                        setText(null);
                    } else {
                        setText(partido.getEquipoVisitanteCodigo());
                    }
                }
            });
        }
        if (btnRegistrar != null) {
            btnRegistrar.setOnAction(event -> onRegistrarResultado());
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
                    cargarJornadas();
                }
            },
            error -> {
                mostrarAlerta(AlertType.WARNING, "Advertencia",
                    "No se pudo cargar la temporada activa: " + error.getMessage());
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
                        .collect(Collectors.toList());

                    tbJornada.setItems(FXCollections.observableArrayList(jornadas));
                },
                error -> {
                    mostrarAlerta(AlertType.ERROR, "Error",
                        "Error al cargar jornadas: " + error.getMessage());
                }
            );
        }
    }

    @FXML
    public void onJornadaSeleccionada() {
        Integer jornadaSeleccionada = tbJornada.getSelectionModel().getSelectedItem();
        if (jornadaSeleccionada != null && idTemporadaActual > 0) {
            partidoService.listarPorJornadaAsync(
                idTemporadaActual,
                jornadaSeleccionada,
                partidos -> {
                    // Filtrar solo partidos pendientes
                    List<PartidoDTO> partidosPendientes = partidos.stream()
                        .filter(p -> "PENDIENTE".equals(p.getEstado()))
                        .collect(Collectors.toList());

                    tbLocal.setItems(FXCollections.observableArrayList(partidosPendientes));
                    tbVisita.setItems(FXCollections.observableArrayList(partidosPendientes));
                },
                error -> {
                    mostrarAlerta(AlertType.ERROR, "Error",
                        "Error al cargar partidos: " + error.getMessage());
                }
            );
        }
    }

    @FXML
    public void onPartidoSeleccionado() {
        partidoSeleccionado = tbLocal.getSelectionModel().getSelectedItem();
        if (partidoSeleccionado != null) {
            tbVisita.getSelectionModel().select(partidoSeleccionado);
        }
    }

    private boolean validarGoles() {
        if (txtGolesL.getText() == null || txtGolesL.getText().trim().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", "Debe ingresar los goles del equipo local");
            return false;
        }
        if (txtGolesV.getText() == null || txtGolesV.getText().trim().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", "Debe ingresar los goles del equipo visitante");
            return false;
        }

        try {
            int golesLocal = Integer.parseInt(txtGolesL.getText().trim());
            int golesVisitante = Integer.parseInt(txtGolesV.getText().trim());

            if (golesLocal < 0 || golesVisitante < 0) {
                mostrarAlerta(AlertType.WARNING, "Advertencia", "Los goles no pueden ser negativos");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", "Los goles deben ser números enteros válidos");
            return false;
        }

        return true;
    }

    @FXML
    public void onRegistrarResultado() {
        try {
            if (partidoSeleccionado == null) {
                mostrarAlerta(AlertType.WARNING, "Advertencia",
                    "Debe seleccionar un partido");
                return;
            }

            if (!validarGoles()) {
                return;
            }

            int golesLocal = Integer.parseInt(txtGolesL.getText().trim());
            int golesVisitante = Integer.parseInt(txtGolesV.getText().trim());

            btnRegistrar.setDisable(true);

            partidoService.registrarResultadoAsync(
                partidoSeleccionado.getId(),
                golesLocal,
                golesVisitante,
                () -> {
                    btnRegistrar.setDisable(false);

                    mostrarAlerta(AlertType.INFORMATION, "Éxito",
                        "Resultado registrado correctamente\n" +
                        partidoSeleccionado.getEquipoLocalCodigo() + " " + golesLocal + " - " +
                        golesVisitante + " " + partidoSeleccionado.getEquipoVisitanteCodigo());

                    txtGolesL.clear();
                    txtGolesV.clear();
                    partidoSeleccionado = null;
                    onJornadaSeleccionada();
                },
                error -> {
                    btnRegistrar.setDisable(false);
                    mostrarAlerta(AlertType.ERROR, "Error",
                        "Error al registrar resultado: " + error.getMessage());
                }
            );

        } catch (NumberFormatException e) {
            mostrarAlerta(AlertType.ERROR, "Error", "Los goles deben ser números válidos");
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error",
                "Error inesperado al registrar el resultado: " + e.getMessage());
        }
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void onVolverClick() {
        org.example.entregable2.servicios.NavigationService.getInstance().mostrarMenuPrincipal();
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
        cargarTemporadaActiva();
    }
}
