package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.entregable2.logica.Liga;

public class GenerarCalendarioController {

    @FXML
    private Label lblInfo;

    @FXML
    private Label lblResultado;

    @FXML
    private Button btnGenerarIdaVuelta;

    @FXML
    private Button btnGenerarSoloIda;

    @FXML
    private Button btnCancelar;

    private Liga liga;

    @FXML
    public void initialize() {
        configurarEventos();
    }

    private void configurarEventos() {
        if (btnGenerarIdaVuelta != null) {
            btnGenerarIdaVuelta.setOnAction(event -> onGenerarIdaVueltaClick());
        }
        if (btnGenerarSoloIda != null) {
            btnGenerarSoloIda.setOnAction(event -> onGenerarSoloIdaClick());
        }
        if (btnCancelar != null) {
            btnCancelar.setOnAction(event -> onCancelarClick());
        }
    }

    @FXML
    public void onGenerarIdaVueltaClick() {
        generarCalendario(true);
    }

    @FXML
    public void onGenerarSoloIdaClick() {
        generarCalendario(false);
    }

    private void generarCalendario(boolean idaVuelta) {
        try {
            if (liga == null) {
                mostrarAlerta(AlertType.ERROR, "Error", "No hay liga configurada");
                return;
            }

            if (liga.getEquipos().size() < 2) {
                mostrarAlerta(AlertType.WARNING, "Advertencia",
                        "Se necesitan al menos 2 equipos para generar el calendario");
                return;
            }

            if (!liga.getCalendario().isEmpty()) {
                Alert confimacion = new Alert(AlertType.CONFIRMATION);
                confimacion.setTitle("Confirmación");
                confimacion.setHeaderText("Ya existe un calendario generado");
                confimacion.setContentText(
                        "¿Desea regenerar el calendario?\n\n" +
                                "ADVERTENCIA: Esto eliminará el calendario actual y " +
                                "reiniciará todas las estadísticas de los equipos " +
                                "(puntos, goles, partidos jugados).");

                java.util.Optional<javafx.scene.control.ButtonType> resultados =
                        confimacion.showAndWait();

                if (resultados.isEmpty() ||
                        resultados.get() != javafx.scene.control.ButtonType.OK) {
                    return;
                }

                liga.reiniciarTemporada();
            }

            liga.generarCalendario(idaVuelta);

            try {
                org.example.entregable2.servicios.PersistenciaService.getInstance().guardarLiga(liga);
            } catch (Exception ex) {
                System.err.println("Error al guardar datos: " + ex.getMessage());
            }

            if (lblResultado != null) {
                String tipo = idaVuelta ? "ida y vuelta" : "solo ida";
                lblResultado.setText("Calendario de " + tipo + " generado exitosamente");
            }
            mostrarAlerta(AlertType.INFORMATION, "Éxito",
                    "El calendario se ha generado correctamente");

        } catch(Exception e){
            mostrarAlerta(AlertType.ERROR, "Error",
                    "Error al generar el calendario: " + e.getMessage());
        }

    }

    @FXML
    public void onCancelarClick() {
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
    }
}

