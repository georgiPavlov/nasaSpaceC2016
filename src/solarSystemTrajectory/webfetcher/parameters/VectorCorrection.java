package solarSystemTrajectory.webfetcher.parameters;

public enum VectorCorrection {
    NONE("NONE"), // Geometric states (no aberration; instantaneous dynamical states)
    ASTRONOMIC_STATES("LT"), // Astrometric states (includes target to obs. light-time aberration)
    APPARENT_STATES("LT+S");// Apparent states (astrometric states with stellar aberration)
    String value;

    VectorCorrection(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}