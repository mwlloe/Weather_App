package weather.model;

/** A city and the coordinates used by the weather service. */
public record TargetLocation(String city, String lat, String lon) { }
