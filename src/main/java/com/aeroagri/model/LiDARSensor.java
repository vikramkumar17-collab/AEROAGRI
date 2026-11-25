package com.aeroagri.model;

import com.aeroagri.exception.SensorException;

/** LiDAR: distance from drone to crop canopy in cm. */
public class LiDARSensor extends Sensor {
    private double baseCm;

    public LiDARSensor(int id, String name, double baseCm) {
        super(id, name);
        this.baseCm = baseCm;
    }

    public double getBaseCm() { return baseCm; }
    public void setBaseCm(double baseCm) { this.baseCm = baseCm; }

    @Override
    public double takeReading() {
        checkOnline();
        double value = round(baseCm + (random.nextDouble() - 0.5), 1); // simulated
        if (value <= 0 || value > 1200) {
            throw new SensorException("LiDAR reading out of range: " + value);
        }
        return value;
    }

    @Override
    public String getUnit() { return "cm"; }

    @Override
    public String getType() { return "LiDAR"; }

    @Override
    public String getDetails() { return "pulse=10kHz"; }
}
