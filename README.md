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


------------------------------------------------------------------------------------------------------------------------------
# CS 342 JavaFX Weather Dashboard — Starter Project

This Maven project is the starter for the JavaFX/FXML Weather Dashboard homework.
It intentionally does **not** contain a completed solution.

## Requirements

- JDK 25
- Maven 3.9+
- Internet access when retrieving current Open-Meteo data

You do **not** need to install the JavaFX SDK separately. Maven downloads the JavaFX modules declared in `pom.xml`.

## Run

```bash
mvn clean javafx:run
```

## Included dependencies

- `org.openjfx:javafx-controls` — JavaFX controls, layouts, charts, shapes, etc.
- `org.openjfx:javafx-fxml` — `FXMLLoader`, FXML controller support, and `@FXML`

Java's `java.net.http` module is part of JDK 25, so it is declared in `module-info.java` but is **not** a Maven dependency.

## Starter structure

```text
src/main/java/
  module-info.java
  weather/model/
    TargetLocation.java
    WeatherReading.java
  weather/service/
    WeatherService.java
  weather/ui/
    WeatherApp.java
    ProgrammaticWeatherView.java
  weather/controller/
    WeatherController.java

src/main/resources/weather/
  weather-view.fxml
  weather.css
```

## Your work

Complete both UI implementations required by the homework:

1. **Programmatic view** — construct the interface in `ProgrammaticWeatherView.java`.
2. **FXML view** — construct the interface in `weather-view.fxml` and implement behavior in `WeatherController.java`.

Both views should use the provided `WeatherService`; do not duplicate HTTP or parsing logic in the UI.

### Visualization hints

Useful built-in JavaFX APIs include:

- `BarChart<String, Number>`
- `CategoryAxis` and `NumberAxis`
- `XYChart.Series` and `XYChart.Data`
- `ProgressBar`
- `Rectangle`, `Circle`, and `Line`
- `Tooltip`
- CSS style classes through `getStyleClass().add(...)` or FXML `styleClass`

No third-party visualization library is required.

## Maven / module notes

`module-info.java` contains:

- `requires javafx.controls;`
- `requires javafx.fxml;`
- `requires java.net.http;`
- `opens weather.controller to javafx.fxml;`

The `opens` directive is important because `FXMLLoader` uses reflection to access the controller's `@FXML` members.