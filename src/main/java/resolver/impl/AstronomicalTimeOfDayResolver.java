package resolver.impl;

import model.DayPeriod;
import model.SunTimes;
import resolver.TimeOfDayResolver;

import java.time.*;

public class AstronomicalTimeOfDayResolver implements TimeOfDayResolver {

    @Override
    public DayPeriod resolvePeriod(double latitude, double longitude) {
        ZonedDateTime now = ZonedDateTime.now();
        SunTimes times = calculateSunTimes(latitude, longitude, now);

        LocalTime time = now.toLocalTime();

        if (time.isBefore(times.civilDawn()) || time.isAfter(times.civilDusk())) return DayPeriod.NIGHT;
        if (time.isBefore(times.sunrise())) return DayPeriod.DAWN;
        if (time.isBefore(times.sunrise().plusHours(2))) return DayPeriod.MORNING;
        if (time.isBefore(times.sunset().minusHours(2))) return DayPeriod.NOON;
        if (time.isBefore(times.sunset())) return DayPeriod.EVENING;
        return DayPeriod.SUNSET;
    }

    private static SunTimes calculateSunTimes(double lat, double lon, ZonedDateTime now) {
        // 1. Days since J2000.0 (January 1, 2000, 12:00 UTC)
        // This is the most critical part for fixing the AM/PM flip.
        Instant j2000 = ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC).toInstant();
        double n = Duration.between(j2000, now.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant()).toDays();

        // 2. Mean Solar Time (J*) centered on longitude
        double jStar = n - (lon / 360.0);

        // 3. Solar Mean Anomaly (M)
        double M = (357.5291 + 0.98560028 * jStar) % 360;
        double mRad = Math.toRadians(M);

        // 4. Equation of Center (C)
        double C = 1.9148 * Math.sin(mRad) + 0.0200 * Math.sin(2 * mRad) + 0.0003 * Math.sin(3 * mRad);

        // 5. Ecliptic Longitude (λ)
        double lambda = (M + C + 180 + 102.9372) % 360;
        double lambdaRad = Math.toRadians(lambda);

        // 6. Solar Transit (The Julian Date of Solar Noon)
        // 2451545.0 is the Julian Date for J2000.0
        double jTransit = 2451545.0 + jStar + 0.0053 * Math.sin(mRad) - 0.0069 * Math.sin(2 * lambdaRad);

        // 7. Declination of the Sun (δ)
        double deltaRad = Math.asin(Math.sin(lambdaRad) * Math.sin(Math.toRadians(23.44)));

        // 8. Calculate Events
        LocalTime sunrise = calculateSolarEvent(jTransit, lat, deltaRad, -0.83, true, now.getZone());
        LocalTime sunset = calculateSolarEvent(jTransit, lat, deltaRad, -0.83, false, now.getZone());
        LocalTime civilDawn = calculateSolarEvent(jTransit, lat, deltaRad, -6.0, true, now.getZone());
        LocalTime civilDusk = calculateSolarEvent(jTransit, lat, deltaRad, -6.0, false, now.getZone());

        return new SunTimes(sunrise, sunset, civilDawn, civilDusk);
    }

    private static LocalTime calculateSolarEvent(double jTransit, double lat, double deltaRad, double angle, boolean isRise, ZoneId zone) {
        double latRad = Math.toRadians(lat);
        double cosOmega = (Math.sin(Math.toRadians(angle)) - Math.sin(latRad) * Math.sin(deltaRad)) /
                          (Math.cos(latRad) * Math.cos(deltaRad));

        // Handle cases where sun never rises/sets
        if (cosOmega > 1) return isRise ? LocalTime.MAX : LocalTime.MIN;
        if (cosOmega < -1) return isRise ? LocalTime.MIN : LocalTime.MAX;

        double omega = Math.toDegrees(Math.acos(cosOmega));
        double julianDate = isRise ? jTransit - (omega / 360.0) : jTransit + (omega / 360.0);

        // Convert Julian Date to LocalTime
        // Subtract J1970 (2440587.5) to get Unix Epoch Days
        long epochMilli = Math.round((julianDate - 2440587.5) * 86400000);
        return Instant.ofEpochMilli(epochMilli).atZone(zone).toLocalTime();
    }
}