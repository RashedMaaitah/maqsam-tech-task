package resolver.impl;

import model.DayPeriod;
import resolver.TimeOfDayResolver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiTimeOfDayResolver implements TimeOfDayResolver {

    private static final String SUNRISE_SUNSET_URL = "https://api.sunrisesunset.io/json?lat=%s&lng=%s";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm:ss a", Locale.ENGLISH);

    @Override
    public DayPeriod resolvePeriod(double latitude, double longitude) {
        try (HttpClient client = HttpClient.newHttpClient()) {

            try {
                String url = String.format(SUNRISE_SUNSET_URL, latitude, longitude);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String json = response.body();

                    if (!json.contains("\"status\":\"OK\"")) {
                        return DayPeriod.NIGHT;
                    }

                    String sunriseStr = extractJsonValue(json, "sunrise");
                    String sunsetStr = extractJsonValue(json, "sunset");
                    String dawnStr = extractJsonValue(json, "dawn");
                    String duskStr = extractJsonValue(json, "dusk");
                    String timezoneStr = extractJsonValue(json, "timezone");

                    ZoneId targetZone = ZoneId.of(timezoneStr);
                    LocalTime remoteTime = LocalTime.now(targetZone);

                    return determinePeriod(remoteTime, sunriseStr, sunsetStr, dawnStr, duskStr);
                }
                return DayPeriod.NIGHT;

            } catch (IOException | InterruptedException e) {
                System.err.println("Connection error: " + e.getMessage());
                return DayPeriod.NIGHT;
            }
        }
    }

    /**
     * Helper to extract a JSON string value using Regex.
     * Searches for "key":"value"
     */
    private String extractJsonValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private DayPeriod determinePeriod(LocalTime now, String sunriseStr, String sunsetStr, String dawnStr, String duskStr) {
        LocalTime sunrise = parseTime(sunriseStr);
        LocalTime sunset = parseTime(sunsetStr);
        LocalTime dawn = parseTime(dawnStr);
        LocalTime dusk = parseTime(duskStr);

        // 1. Night: Before civil dawn or after civil dusk
        if (now.isBefore(dawn) || now.isAfter(dusk)) return DayPeriod.NIGHT;

        // 2. Sunrise: Between civil dawn and actual sunrise
        if (now.isBefore(sunrise)) return DayPeriod.DAWN;

        // 3. Morning: Sunrise to 2 hours after sunrise
        if (now.isBefore(sunrise.plusHours(2))) return DayPeriod.MORNING;

        // 4. Noon (Mid-day): From Morning end until 2 hours before sunset
        if (now.isBefore(sunset.minusHours(2))) return DayPeriod.NOON;

        // 5. Evening: 2 hours before sunset until sunset
        if (now.isBefore(sunset)) return DayPeriod.EVENING;

        // 6. Sunset: Between sunset and civil dusk
        return DayPeriod.SUNSET;
    }

    private LocalTime parseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (Exception e) {
            return LocalTime.MIDNIGHT;
        }
    }
}