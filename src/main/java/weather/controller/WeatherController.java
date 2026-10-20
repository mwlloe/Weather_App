package weather.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import weather.model.TargetLocation;
import weather.model.WeatherReading;
import weather.service.WeatherService;

import java.util.ArrayList;
import java.util.List;

/** Controller for weather-view.fxml. */
public class WeatherController {

    private WeatherService weatherService = new WeatherService();
    private List<WeatherReading> latestReadings = new ArrayList<>();
    private Runnable goHome;

    // these names have to match the fx:id's in weather-view.fxml
    @FXML
    private Label statusLabel;
    @FXML
    private Label detailLabel;
    @FXML
    private ComboBox<String> cityBox;
    @FXML
    private VBox progressBox;
    @FXML
    private BarChart<String, Number> temperatureChart;

    // WeatherApp calls this so the Back button knows where to go
    public void setGoHome(Runnable goHome) {
        this.goHome = goHome;
    }

    // runs by itself when the screen loads
    @FXML
    private void initialize() {
        statusLabel.setText("Select Refresh Weather to load data.");
        for (TargetLocation location : weatherService.getLocations()) {
            cityBox.getItems().add(location.city());
        }
        handleRefresh();
    }

    /** refresh FXML event-handler */
    @FXML
    private void handleRefresh() {
        try {
            List<WeatherReading> readings = weatherService.getCurrentWeather();
            updateDisplay(readings);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // dropdown
    @FXML
    private void handleCitySelected() {
        showSelectedCity();
    }

    // back button
    @FXML
    private void handleBack() {
        if (goHome != null) {
            goHome.run();
        }
    }

    private void updateDisplay(List<WeatherReading> readings) {
        latestReadings = readings;

        String text = "";
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Temperature (°F)");
        progressBox.getChildren().clear();

        for (WeatherReading reading : readings) {
            String city = reading.location().city();
            double temp = reading.temperatureF();

            text = text + city + ": " + temp + "°F\n";
            series.getData().add(new XYChart.Data<>(city, temp));

            double progress = temp / 120.0;
            if (progress < 0) {
                progress = 0;
            }
            if (progress > 1) {
                progress = 1;
            }
            ProgressBar bar = new ProgressBar(progress);
            bar.setPrefWidth(200);
            Label cityName = new Label(city);
            cityName.setPrefWidth(100);
            progressBox.getChildren().add(new HBox(10, cityName, bar));
        }

        statusLabel.setText(text);
        temperatureChart.getData().clear();
        temperatureChart.getData().add(series);
        showSelectedCity();
    }

    private void showSelectedCity() {
        String selected = cityBox.getValue();
        if (selected == null) {
            return;
        }

        for (WeatherReading reading : latestReadings) {
            if (reading.location().city().equals(selected)) {
                detailLabel.setText(selected + " is " + reading.temperatureF()
                + "°F (lat " + reading.location().lat()
                + ", lon " + reading.location().lon() + ")");
                return;
            }
        }
        detailLabel.setText("No weather data yet for " + selected + ". Press Refresh Weather.");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("⚠️Could not load weather!");
        alert.setContentText(message);
        alert.showAndWait();
    }
    // picks a css class name based on how warm it is; recopied from programmatic
    private String getTempClass(double temp) {
        if (temp < 40) {
            return "cold";
        } else if (temp < 65) {
            return "mild";
        } else if (temp < 85) {
            return "warm";
        } else {
            return "hot";
        }
    } 
}