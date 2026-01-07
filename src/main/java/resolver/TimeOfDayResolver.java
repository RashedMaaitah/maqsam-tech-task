package resolver;

import model.DayPeriod;

public interface TimeOfDayResolver {
    DayPeriod resolvePeriod(double latitude, double longitude);
}
