package solarSystemTrajectory.webfetcher.parameters;

public enum OutputUnit {
    AU_PER_DAY("AU-D"), // velocity in AU/d
    KILOMETER_PER_DAY("KM-D"), // velocity in km/d
    KILOMETER_PER_SECOND("KM-S");// velocity in km/s
    String value;

    OutputUnit(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
