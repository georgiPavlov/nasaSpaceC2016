package solarSystemTrajectory.webfetcher.parameters;


public enum ReferenceSystem {
    J2000("J2000"), // ICRF/J2000.0
    B1950("B1950");// FK4/B1950.0
    String value;

    ReferenceSystem(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}