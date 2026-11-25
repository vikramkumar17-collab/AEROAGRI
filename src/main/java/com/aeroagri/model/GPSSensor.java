package com.aeroagri.model;

/** GPS: latitude/longitude of the drone (simulated around a fixed field point). */
public class GPSSensor extends Sensor {
    private double baseLat;
    private double baseLon;
    private double lastLon;

    // Overloaded constructors
    public GPSSensor(int id, String name) {
        this(id, name, 28.9931, 77.0151);
    }

    public GPSSensor(int id, String name, double baseLat, double baseLon) {
        super(id, name);
        this.baseLat = baseLat;
        this.baseLon = baseLon;
        this.lastLon = baseLon;
    }

    public double getLongitude() { return lastLon; }

    @Override
    public double takeReading() {
        checkOnline();
        lastLon = round(baseLon + (random.nextDouble() - 0.5) * 0.0002, 6);
        return round(baseLat + (random.nextDouble() - 0.5) * 0.0002, 6); // latitude
    }

    @Override
    public String getUnit() { return "deg"; }

    @Override
    public String getType() { return "GPS"; }

    @Override
    public String getDetails() { return "lon=" + lastLon + ";satellites=18;hdop=0.78"; }
}
