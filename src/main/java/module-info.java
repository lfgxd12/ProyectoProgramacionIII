module org.example.entregable2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.entregable2 to javafx.fxml;
    exports org.example.entregable2;

    opens org.example.entregable2.controllers to javafx.fxml;
    exports org.example.entregable2.controllers;

    opens org.example.entregable2.logica to javafx.fxml, javafx.base;
    exports org.example.entregable2.logica;

    exports org.example.entregable2.servicios;
}

