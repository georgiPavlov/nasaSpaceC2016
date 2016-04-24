package solarSystemTrajectory.webfetcher.parameters;

public enum QuantityCode {
    POSTION_COMPONENTS("1"), // position components {x,y,z} only
    STATE_VECTORS("2"), // state vector {x,y,z,vx,vy,vz}
    STATE_VECTORS_ONE_WAY_LIGHT_TIME("3"), // state vector, 1-way light-time, range, and range-rate
    POSTION_ONE_WAY_LIGHT_TIME("4"), // position, 1-way light-time, range, and range-rate)
    VELOCITY_COMPONENTS("5"), // velocity components {vx,vy,vz} only
    ONE_WAY_LIGHT_TIME("6");// 1-way light-time, range, and range-rate

    String value;

    QuantityCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

