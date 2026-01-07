package model;

public enum DayPeriod {
    NIGHT("night.png"),
    DAWN("sunrise.png"),
    MORNING("morning.png"),
    NOON("day.png"),
    EVENING("afternoon.png"),
    SUNSET("sunset.png");

    private final String fileName;

    DayPeriod(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}