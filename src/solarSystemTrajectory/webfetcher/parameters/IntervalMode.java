package solarSystemTrajectory.webfetcher.parameters;

public enum IntervalMode {
    DAYS("d"), // days
    HOURS("h"), // hours
    MINUTES("m"), // minutes
    EQUAL_INTERVALS("f"), // equal intervals (unitless)
    ARCSECOND("VAR"), // arcseconds (time-varying)
    CALENDAR_YEARS("Y"), // calendar years
    CALENDAR_MONTHS("MO");// calendar months

    String value;

    IntervalMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
