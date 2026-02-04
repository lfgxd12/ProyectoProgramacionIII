package org.example.entregable2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.entregable2.logica.Equipo;
import org.example.entregable2.logica.Liga;

public class RegistrarEquipoController {

    @FXML
    private TextField txtNombreEquipo;

    @FXML
    private TextField txtAnioFundacion;

    @FXML
    private TextField txtNombreEstadio;

    @FXML
    private TextField txtCiudad;
    @FXML
    private TextField txtCodigo;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnLimpiarCampos;

    @FXML
    private Button btnVolver;

    private Liga liga;

    @FXML
    public void initialize() {
    }

    private boolean validarDatos() {
        if (txtNombreEquipo.getText() == null || txtNombreEquipo.getText().trim().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", "El nombre del equipo es obligatorio");
            return false;
        }
        if (txtAnioFundacion.getText() == null || txtAnioFundacion.getText().trim().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", "El año de fundación es obligatorio");
            return false;
        }
        if (txtNombreEstadio.getText() == null || txtNombreEstadio.getText().trim().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", "El nombre del estadio es obligatorio");
            return false;
        }
        if (txtCiudad.getText() == null || txtCiudad.getText().trim().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", "El nombre de la ciudad es obligatorio");
            return false;
        }

        if (txtCodigo.getText() == null || txtCodigo.getText().trim().isEmpty()){
            mostrarAlerta(AlertType.WARNING, "Advertencia", "El código del equipo es obligatorio");
            return false;
        }

        if (txtCodigo.getText().trim().length() < 3){
            mostrarAlerta(AlertType.WARNING, "Advertencia", "El código del equipo debe tener al menos 3 caracteres");
            return false;
        }

        try {
            Integer.parseInt(txtAnioFundacion.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(AlertType.WARNING, "Advertencia",
                "El año de fundación debe ser un número válido");
            return false;
        }

        return true;
    }

    @FXML
    public void onGuardarClick() {
        try {
            if (liga == null) {
                mostrarAlerta(AlertType.ERROR, "Error", "No hay liga configurada");
                return;
            }

            if (!validarDatos()) {
                return;
            }

            String nombre = txtNombreEquipo.getText().trim();
            String ciudad = txtCiudad.getText().trim();
            String codigo = txtCodigo.getText().trim().toUpperCase();

            Equipo equipo = new Equipo(nombre, ciudad, codigo);

            liga.registrarEquipo(equipo);

            try {
                org.example.entregable2.servicios.PersistenciaService.getInstance().guardarLiga(liga);
            } catch (Exception ex) {
                System.err.println("Error al guardar datos: " + ex.getMessage());
            }

            mostrarAlerta(AlertType.INFORMATION, "Éxito",
                "Equipo registrado correctamente");

            onLimpiarCamposClick();

        } catch (IllegalArgumentException e) {
            mostrarAlerta(AlertType.ERROR, "Error", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error",
                "Error inesperado al registrar el equipo: " + e.getMessage());
        }
    }

    @FXML
    public void onLimpiarCamposClick() {
        txtNombreEquipo.clear();
        txtAnioFundacion.clear();
        txtNombreEstadio.clear();
        txtCiudad.clear();
        txtCodigo.clear();
    }

    @FXML
    public void onVolverClick() {
        org.example.entregable2.servicios.NavigationService.getInstance().mostrarListaEquipos();
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
