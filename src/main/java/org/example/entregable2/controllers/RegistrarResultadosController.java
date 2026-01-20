package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.logica.Partido;

public class RegistrarResultadosController {

    @FXML
    private ListView<Integer> tbJornada;

    @FXML
    private ListView<Partido> tbLocal;

    @FXML
    private ListView<Partido> tbVisita;

    @FXML
    private TextField txtGolesL;

    @FXML
    private TextField txtGolesV;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnVolver;

    private Liga liga;
    private Partido partidoSeleccionado;

    @FXML
    public void initialize() {
        configurarEventos();
        cargarJornadas();
    }

    private void configurarEventos() {
        if (tbJornada != null) {
            tbJornada.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onJornadaSeleccionada());
        }
        if (tbLocal != null) {
            tbLocal.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onPartidoSeleccionado());
        }
        if (btnRegistrar != null) {
            btnRegistrar.setOnAction(event -> onRegistrarResultado());
        }
        if (btnVolver != null) {
            btnVolver.setOnAction(event -> onVolverClick());
        }
    }

    private void cargarJornadas() {
        if (liga != null) {
            java.util.Map<Integer, java.util.List<Partido>> partidosPorJornada = liga.getPartidosPorJornada();
            javafx.collections.ObservableList<Integer> jornadas =
                javafx.collections.FXCollections.observableArrayList(partidosPorJornada.keySet());
            tbJornada.setItems(jornadas.sorted());
        }
    }

    @FXML
    public void onJornadaSeleccionada() {
        Integer jornadaSeleccionada = tbJornada.getSelectionModel().getSelectedItem();
        if (jornadaSeleccionada != null && liga != null) {
            java.util.List<Partido> partidos = liga.listarPartidosPorJornada(jornadaSeleccionada);
            java.util.List<Partido> partidosPendientes = partidos.stream()
                .filter(p -> !p.tieneResultado())
                .filter(p -> !p.getLocal().isEliminado() && !p.getVisitante().isEliminado())
                .collect(java.util.stream.Collectors.toList());
            javafx.collections.ObservableList<Partido> partidosObservable =
                javafx.collections.FXCollections.observableArrayList(partidosPendientes);
            tbLocal.setItems(partidosObservable);
            tbVisita.setItems(partidosObservable);
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

            Partido partido = liga.obtenerPartidoPorId(partidoSeleccionado.getId());

            if (partido == null) {
                mostrarAlerta(AlertType.ERROR, "Error", "Partido no encontrado");
                return;
            }

            if (partido.tieneResultado()) {
                mostrarAlerta(AlertType.ERROR, "Error",
                    "Este partido ya tiene un resultado registrado");
                return;
            }

            liga.registrarResultadoPartido(partido.getId(), golesLocal, golesVisitante);

            try {
                org.example.entregable2.servicios.PersistenciaService.getInstance().guardarLiga(liga);
            } catch (Exception ex) {
                System.err.println("Error al guardar datos: " + ex.getMessage());
            }

            mostrarAlerta(AlertType.INFORMATION, "Éxito",
                "Resultado registrado correctamente\n" +
                partido.getLocal().getNombre() + " " + golesLocal + " - " +
                golesVisitante + " " + partido.getVisitante().getNombre());

            txtGolesL.clear();
            txtGolesV.clear();
            partidoSeleccionado = null;
            cargarJornadas();
            onJornadaSeleccionada();

        } catch (IllegalArgumentException e) {
            mostrarAlerta(AlertType.ERROR, "Error", e.getMessage());
        } catch (IllegalStateException e) {
            mostrarAlerta(AlertType.ERROR, "Error", e.getMessage());
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
        cargarJornadas();
    }
}
