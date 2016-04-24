package solarSystemTrajectory.webfetcher.parameters;
public enum EphemerisType {
    OBSERVER("OBSERVER"), // Observer
    VECTORS("VECTORS"), // Vector Table
    ELEMENTS("ELEMENTS");// Orbital Elements
    String value;

    EphemerisType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
