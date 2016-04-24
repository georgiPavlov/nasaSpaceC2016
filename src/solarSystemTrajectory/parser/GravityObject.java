package solarSystemTrajectory.parser;


public class GravityObject {
    private String id;
    private String name;
    private double density;//density in kg/m3
    private double mass;//mass in kilograms
    private GravityObjectType type;
    private double x;
    private double y;
    private double z;
    private double vx;
    private double vy;
    private double vz;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public GravityObjectType getType() {
        return type;
    }

    public void setType(GravityObjectType type) {
        this.type = type;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getVz() {
        return vz;
    }

    public void setVz(double vz) {
        this.vz = vz;
    }

}