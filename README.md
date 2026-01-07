# Maqsam Task - Sun Position Wallpaper Selector
### [GitHub Repo](https://github.com/RashedMaaitah/maqsam-tech-task.git)

A Java-based utility that selects a desktop wallpaper filename representing the sun's position at the time of execution for a specific geographical location.

## Requirements

- **Java 21** or higher
- **Maven 3.6+**
- **Linux Bash** environment

## Usage

### Using the Wrapper Script

The easiest way to run the program is using the provided `run.sh` script, which handles compilation and execution via Maven.

```bash
chmod +x run.sh
./run.sh <latitude> <longitude>
```

### Direct Maven Execution

You can also run the program directly using Maven commands:

```bash
mvn clean compile exec:java -q -Dexec.mainClass="Main" -Dexec.args="31.9544 35.9106"
```

## Examples

Below are the expected outputs for specific coordinates.  
Note: Results depend on the time of execution.

| Location                | Latitude  | Longitude | Expected Output |
|-------------------------|-----------|-----------|-----------------|
| Amman, Jordan           | 31.9544   | 35.9106   | morning.png     |
| Melbourne, Australia    | -37.8136  | 144.9631  | sunrise.png     |
| Rio Gallegos, Argentina | -51.63092 | -69.2247  | night.png       |

## How It Works

1. **Coordinate-Aware Timing**  
   The program retrieves solar data (sunrise, sunset, dawn, and dusk) for the provided coordinates and identifies the local timezone of that location to ensure accurate time comparison.

2. **Solar Mapping Logic**
    - `night.png`: Sun is below the horizon (before dawn or after dusk)
    - `sunrise.png`: Dawn period (between civil dawn and sunrise)
    - `morning.png`: Early morning (sunrise up to 2 hours after)
    - `noon.png`: Mid-day (from morning end until 2 hours before sunset)
    - `evening.png`: Late afternoon (2 hours before sunset until sunset)
    - `sunset.png`: Sunset period (between sunset and civil dusk)

## Implementation Details

- **Zero External Dependencies**: Implemented using pure Java 21 Standard Library (no Jackson or external JSON libraries)
- **Timezone Handling**: Uses the `java.time` API to compare the current machine time against the target location's specific timezone provided by the API
- **Maven Lifecycle**: Uses a standard Maven project structure for clean builds and execution

## Project Structure

```text
FirstName_LastName_EmailAddress_Date/
├── pom.xml              # Maven configuration
├── run.sh               # Execution script
├── README.md            # Documentation
└── src/
    └── main/
        └── java/
            ├── Main.java
            ├── model/
            │   ├── DayPeriod.java
            │   └── SunTimes.java
            └── resolver/
                ├── TimeOfDayResolver.java
                └── impl/
                    ├── ApiTimeOfDayResolver.java
                    └── AstronomicalTimeOfDayResolver.java
```
