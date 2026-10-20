package weather.service;

import weather.model.TargetLocation;
import weather.model.WeatherReading;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared weather/data code for BOTH user interfaces.
 * Students should not duplicate this HTTP/parsing code in controllers or views.
 */
public class WeatherService {
    private static final Pattern TEMPERATURE_PATTERN =
            Pattern.compile("\\\"temperature_2m\\\":\\s*([0-9.-]+)");

    private final List<TargetLocation> locations = List.of(
            new TargetLocation("Chicago", "41.85", "-87.65"),
            new TargetLocation("Los Angeles", "34.05", "-118.24"),
            new TargetLocation("New York", "40.71", "-74.01")
    );

    public List<TargetLocation> getLocations() {
        return locations;
    }

    public List<WeatherReading> getCurrentWeather() throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            return locations.stream().map(location -> {
                try {
                    return getCurrentWeather(client, location);
                } catch (Exception e) {
                    if (e instanceof java.io.IOException) {
                        // couldn't connect at all, so probably no internet
                        throw new WeatherRetrievalException(null, e);
                    } else {
                        throw new WeatherRetrievalException(location.city(), e);
                    }
                }
            }).toList();
        } catch (WeatherRetrievalException e) {
            throw e;
        }
    }

    private WeatherReading getCurrentWeather(HttpClient client, TargetLocation target) throws Exception {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + target.lat()
                + "&longitude=" + target.lon()
                + "&current=temperature_2m&temperature_unit=fahrenheit";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException("API returned HTTP " + response.statusCode());
        }

        return new WeatherReading(target, parseTemperature(response.body()));
    }

    static double parseTemperature(String json) {
        Matcher matcher = TEMPERATURE_PATTERN.matcher(json);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        throw new IllegalArgumentException("temperature_2m was not found in the API response");
    }

    // custom error: if city is null, we show the general "no internet" message
    private static class WeatherRetrievalException extends RuntimeException {
        WeatherRetrievalException(String city, Throwable cause) {
            super(makeMessage(city, cause), cause);
        }

        // builds the message text (has to be static so super(...) can use it)
        private static String makeMessage(String city, Throwable cause) {
            if (city == null) {
                return "Could not retrieve weather!" + "\nCheck to make sure you are connected to the internet." + "\nError: " + cause.getMessage();
            } else {
                return "Could not retrieve weather for " + city + ":" + "\n" + cause.getMessage();
            }
        }
    }
}
