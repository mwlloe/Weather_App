module weather {
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;

    exports weather.model;
    exports weather.service;
    exports weather.ui;

    // FXMLLoader uses reflection to inject @FXML fields and call handlers.
    opens weather.controller to javafx.fxml;
}
