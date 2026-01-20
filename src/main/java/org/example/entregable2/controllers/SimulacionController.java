package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.example.entregable2.logica.Liga;
import org.example.entregable2.servicios.PersistenciaService;

public class SimulacionController {

    @FXML
    private Label lblEquiposRegistrados;

    @FXML
    private Label lblTotalJornadas;

    @FXML
    private Label lblJornadasJugadas;

    @FXML
    private Label lblJornadasPendientes;

    @FXML
    private Spinner<Integer> spinnerCantidad;

    @FXML
    private Button btnSimularUna;

    @FXML
    private Button btnSimularVarias;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnVolver;

    private Liga liga;

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spinnerCantidad.setValueFactory(valueFactory);
    }

    @FXML
    public void onSimularUnaClick() {
        if (liga == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No hay liga configurada");
            return;
        }

        try {
            if (!liga.puedeSimularJornadas()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                    "No hay jornadas disponibles para simular. Genera un calendario primero.");
                return;
            }

            liga.simularJornada();

            PersistenciaService.getInstance().guardarLiga(liga);

            actualizarEstadisticas();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                "Jornada simulada correctamente");

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                "Error al simular jornada: " + e.getMessage());
        }
    }

    @FXML
    public void onSimularVariasClick() {
        if (liga == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No hay liga configurada");
            return;
        }

        try {
            int cantidad = spinnerCantidad.getValue();

            if (!liga.puedeSimularJornadas()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                    "No hay jornadas disponibles para simular. Genera un calendario primero.");
                return;
            }

            liga.simularVariasJornadas(cantidad);

            PersistenciaService.getInstance().guardarLiga(liga);

            actualizarEstadisticas();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                cantidad + " jornada(s) simulada(s) correctamente");

        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                "Error al simular jornadas: " + e.getMessage());
        }
    }

    @FXML
    public void onActualizarClick() {
        actualizarEstadisticas();
    }

    @FXML
    public void onVolverClick() {
        org.example.entregable2.servicios.NavigationService.getInstance().mostrarMenuPrincipal();
    }

    private void actualizarEstadisticas() {
        if (liga != null) {
            int equipos = liga.getEquipos().size();
            int totalJornadas = liga.calcularTotalJornadas();
            int jugadas = liga.contarJornadasJugadas();
            int pendientes = liga.contarJornadasPendientes();

            lblEquiposRegistrados.setText("Equipos registrados: " + equipos);
            lblTotalJornadas.setText("Total de jornadas: " + totalJornadas);
            lblJornadasJugadas.setText("Jornadas jugadas: " + jugadas);
            lblJornadasPendientes.setText("Jornadas pendientes: " + pendientes);

            int maxSimular = Math.min(pendientes, 100);
            SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Math.max(1, maxSimular), 1);
            spinnerCantidad.setValueFactory(valueFactory);
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
        actualizarEstadisticas();
    }
}

