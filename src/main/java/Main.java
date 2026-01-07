import model.DayPeriod;
import resolver.TimeOfDayResolver;
import resolver.impl.ApiTimeOfDayResolver;
import resolver.impl.AstronomicalTimeOfDayResolver;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Main <latitude> <longitude>");
            System.exit(1);
        }
        DayPeriod wallpaper = getDayPeriod(args);

        System.out.println(wallpaper.getFileName());
    }

    private static DayPeriod getDayPeriod(String[] args) {
        double latitude = Double.parseDouble(args[0]);
        double longitude = Double.parseDouble(args[1]);

        // Choose implementation:
        // Option 1: Astronomical calculations (offline-ish, most accurate)
        // TimeOfDayResolver resolver = new AstronomicalTimeOfDayResolver();

        // Option 2: API-based (requires internet, simpler)
        TimeOfDayResolver resolver = new ApiTimeOfDayResolver();

        return resolver.resolvePeriod(latitude, longitude);
    }
}