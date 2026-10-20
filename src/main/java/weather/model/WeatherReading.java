package weather.model;

/** Data the UI can display without knowing how it was retrieved. */
public record WeatherReading(TargetLocation location, double temperatureF) { }
