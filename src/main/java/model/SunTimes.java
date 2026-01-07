package model;

import java.time.LocalTime;

public record SunTimes(LocalTime sunrise, LocalTime sunset, LocalTime civilDawn, LocalTime civilDusk) {
}