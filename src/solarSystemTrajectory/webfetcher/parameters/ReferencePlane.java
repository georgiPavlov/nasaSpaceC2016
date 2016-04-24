package solarSystemTrajectory.webfetcher.parameters;


public enum ReferencePlane {
    ECLIPTIC("ECLIPTIC"), // ecliptic and mean equinox of reference epoch
    FRAME("FRAME"), // Earth mean equator and equinox of reference epoch
    BODY_EQUATOR("BODY EQUATOR");// object mean equator and node of date
    String value;

    ReferencePlane(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}