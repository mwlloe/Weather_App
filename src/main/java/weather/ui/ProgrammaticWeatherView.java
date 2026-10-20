package weather.ui;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import weather.model.TargetLocation;
import weather.model.WeatherReading;
import weather.service.WeatherService;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Complete the programmatic version of the assignment here.
 * Do NOT use FXML in this class.
 */
public class ProgrammaticWeatherView extends BorderPane {

    private WeatherService weatherService = new WeatherService();

    // these are up here so refresh() can use them later
    private Label statusLabel = new Label("Press Refresh Weather to load data.");
    private Label detailLabel = new Label("Pick a city after refreshing.");
    private ComboBox<String> cityBox = new ComboBox<>();
    private VBox progressBox = new VBox(8);
    private BarChart<String, Number> temperatureChart =
            new BarChart<>(new CategoryAxis(), new NumberAxis());

    // saves the last weather we got so the dropdown can use it
    private List<WeatherReading> latestReadings = new ArrayList<>();

    public ProgrammaticWeatherView(Runnable goHome) {
        Label title = new Label("Programmatic Weather View");
        title.getStyleClass().add("title");
        title.setWrapText(true);

        // runs automatic first time results
        refresh();

        Button refreshButton = new Button("Refresh Weather");
        refreshButton.getStyleClass().add("refresh-button");
        refreshButton.setOnAction(event -> refresh());

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> goHome.run());

        // put the city names in the dropdown
        for (TargetLocation location : weatherService.getLocations()) {
            cityBox.getItems().add(location.city());
        }
        cityBox.setPromptText("Choose a city");
        cityBox.setOnAction(event -> showSelectedCity());

        temperatureChart.setTitle("City Temperatures (°F)");
        temperatureChart.setAnimated(false);

        HBox buttons = new HBox(10, refreshButton, backButton);
        VBox left = new VBox(15, title, buttons, cityBox, detailLabel, statusLabel, progressBox);
        left.getStyleClass().add("content-panel");
        left.setPrefWidth(360);

        setLeft(left);
        setCenter(temperatureChart);
        setBottom(buttons);
    }

    // gets the weather, or shows an error popup if it fails
    private void refresh() {
        try {
            List<WeatherReading> readings = weatherService.getCurrentWeather();
            updateDisplay(readings);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // puts the weather into the label, chart, and progress bars
    private void updateDisplay(List<WeatherReading> readings) {
        latestReadings = readings;

        String text = "";
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Temperature (°F)");
        progressBox.getChildren().clear();

        // loop through each city
        for (WeatherReading reading : readings) {
            String city = reading.location().city();
            double temp = reading.temperatureF();

            // text for the label
            text = text + city + ": " + temp + "°F\n";

            // point for the bar chart
            series.getData().add(new XYChart.Data<>(city, temp));

            // progress bar goes from 0 to 1, so divide by 120 degrees
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

            String tempClass = getTempClass(temp);
            bar.getStyleClass().add(tempClass);
            cityName.getStyleClass().add(tempClass);

            progressBox.getChildren().add(new HBox(10, cityName, bar));
        }

        statusLabel.setText(text);
        temperatureChart.getData().clear();
        temperatureChart.getData().add(series);
        showSelectedCity();
    }

    // shows info for whatever city is picked in the dropdown
    private void showSelectedCity() {
        String selected = cityBox.getValue();
        if (selected == null) {
            return; // nothing picked yet
        }

        for (WeatherReading reading : latestReadings) {
            if (reading.location().city().equals(selected)) {
                detailLabel.setText(selected + " is " + reading.temperatureF()
                        + "°F (lat " + reading.location().lat()
                        + ", lon " + reading.location().lon() + ")");
                return;
            }
        }
        detailLabel.setText("No data yet for " + selected + ". Press Refresh Weather.");
    }

    // popup for errors
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("⚠️Could not load weather.");
        alert.setContentText(message);
        alert.showAndWait();
    }
    // picks a css class name based on how warm it is
    private String getTempClass(double temp) {
        if (temp < 55) {
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