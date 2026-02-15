package org.example.entregable2.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.entregable2.dto.EquipoDTO;
import org.example.entregable2.logica.Equipo;
import org.example.entregable2.logica.EquipoLogica;
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
        // Validar antes de iniciar la tarea asíncrona
        if (!validarDatos()) {
            return;
        }

        // Capturar valores de la UI en el hilo principal
        final String nombre = txtNombreEquipo.getText().trim();
        final String ciudad = txtCiudad.getText().trim();
        final String codigo = txtCodigo.getText().trim().toUpperCase();
        final String estadio = txtNombreEstadio.getText().trim();
        final int anioFundacion = Integer.parseInt(txtAnioFundacion.getText().trim());

        // Crear DTO para enviar al servidor
        EquipoDTO dto = new EquipoDTO();
        dto.setCodigo(codigo);
        dto.setNombre(nombre);
        dto.setCiudad(ciudad);
        dto.setEstadio(estadio);
        dto.setAnioFundacion(anioFundacion);

        // Deshabilitar botón durante la operación
        btnGuardar.setDisable(true);

        // Crear tarea asíncrona para no bloquear la UI
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                EquipoLogica logica = new EquipoLogica();
                return logica.crearEquipo(dto);
            }
        };

        // Manejar éxito
        task.setOnSucceeded(event -> {
            int idEquipo = task.getValue();

            // Actualizar instancia local de Liga si existe
            if (liga != null) {
                try {
                    Equipo equipoLocal = new Equipo(nombre, ciudad, codigo);
                    equipoLocal.setEstadio(estadio);
                    equipoLocal.setAnnioFundacion(String.valueOf(anioFundacion));
                    liga.registrarEquipo(equipoLocal);
                } catch (Exception e) {
                    System.err.println("Advertencia: No se pudo actualizar Liga local: " + e.getMessage());
                }
            }

            mostrarAlerta(AlertType.INFORMATION, "Éxito",
                "Equipo registrado correctamente con ID: " + idEquipo);
            onLimpiarCamposClick();
            btnGuardar.setDisable(false);
        });

        // Manejar error
        task.setOnFailed(event -> {
            Throwable error = task.getException();
            String mensaje = error.getMessage();

            // Mensajes amigables
            if (mensaje.contains("Ya existe")) {
                mostrarAlerta(AlertType.WARNING, "Equipo Duplicado", mensaje);
            } else if (mensaje.contains("Sin respuesta")) {
                mostrarAlerta(AlertType.ERROR, "Error de Conexión",
                    "No se pudo conectar con el servidor. Verifique que esté ejecutándose.");
            } else {
                mostrarAlerta(AlertType.ERROR, "Error",
                    "Error al registrar el equipo: " + mensaje);
            }

            btnGuardar.setDisable(false);
        });

        // Ejecutar tarea en un nuevo hilo
        new Thread(task).start();
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
