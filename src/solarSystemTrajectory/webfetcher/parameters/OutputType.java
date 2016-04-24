package solarSystemTrajectory.webfetcher.parameters;

public enum OutputType {
    HTML("HTML"), //
    TEXT("TEXT"), //
    SAVE("SAVE"); // useful for us ?
    String value;

    OutputType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
