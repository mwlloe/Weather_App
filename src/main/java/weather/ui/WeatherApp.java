/*
Copyright statement for my code
# =============================================================================
# Copyright (c) 2026 Miguel Rodriguez. All Rights Reserved.
#
# This software and its source code are proprietary and confidential.
# No part of this program may be reproduced, modified, reverse engineered,
# distributed, or used without the express written permission of
# Miguel Rodriguez.
#
# Unauthorized use may violate applicable copyright and intellectual
# property laws.
# =============================================================================
*/

package weather.ui;

import weather.controller.WeatherController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Starter application shell.
 *
 * The small launcher/navigation shell is provided. Students complete BOTH
 * required weather dashboard views.
 */
public class WeatherApp extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;             // <--- WINDOW (stage)
        stage.setTitle("CS 342 Weather Dashboard");
        showHome();                     // <---- Builds HOME SCREEN
        stage.show();
    }

    private void showHome() {
        Label title = new Label("Real-time Weather App");      // <---- SCREEN TEXT
        title.getStyleClass().add("title");                         // <---- TAG so i can style it within CSS file

        Label directions = new Label(
                "This Real-time Weather App displays the current weather for select cities twice: once programmatically and once with FXML."
        );
        directions.setWrapText(true);

        Button programmaticButton = new Button("Programmatic View");
        programmaticButton.setOnAction(event -> showProgrammaticView());

        Button fxmlButton = new Button("FXML View");
        fxmlButton.setOnAction(event -> showFxmlView());
        
        // buttons are set towards the bottom
        HBox buttons = new HBox(12, programmaticButton, fxmlButton);
        buttons.getStyleClass().add("button-bar");

        VBox center = new VBox(18, title, directions);
        center.getStyleClass().add("home-panel");

        BorderPane root = new BorderPane(center);
        root.setBottom(buttons);
        setScene(root);
    }

    private void showProgrammaticView() {
        ProgrammaticWeatherView view = new ProgrammaticWeatherView(this::showHome);
        setScene(view);
    }

    private void showFxmlView() {
        try {
            FXMLLoader loader = new FXMLLoader(WeatherApp.class.getResource("/weather/weather-view.fxml"));
            Parent root = loader.load();
            WeatherController controller = loader.getController();      // <---- lets the Back button in the FXML view find its way home
            controller.setGoHome(this::showHome);
            setScene(root);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not load FXML view");
            alert.setContentText(e.getMessage());
            alert.showAndWait();            // <---- SHOWS POP UP BOX UNTIL USER CLOSES IT 
        }
    }

    private void setScene(Parent root) {
        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(WeatherApp.class.getResource("/weather/weather.css").toExternalForm());
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}