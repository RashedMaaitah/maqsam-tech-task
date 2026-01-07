package resolver.impl;

import model.DayPeriod;
import org.junit.jupiter.api.Test;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

public class AstronomicalTimeOfDayResolverTest {

    @Test
    public void testResolvePeriodUsesCorrectLocalTime() {
        AstronomicalTimeOfDayResolver resolver = new AstronomicalTimeOfDayResolver();
        
        // Amman, Jordan
        double lat = 31.9544;
        double lon = 35.9106;
        
        DayPeriod period = resolver.resolvePeriod(lat, lon);
        assertNotNull(period);
        System.out.println("Period for Amman: " + period);
    }

    @Test
    public void testResolvePeriodForMelbourne() {
        AstronomicalTimeOfDayResolver resolver = new AstronomicalTimeOfDayResolver();
        
        // Melbourne, Australia
        double lat = -37.8136;
        double lon = 144.9631;
        
        DayPeriod period = resolver.resolvePeriod(lat, lon);
        assertNotNull(period);
        System.out.println("Period for Melbourne: " + period);
    }
}
