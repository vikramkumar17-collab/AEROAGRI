package com.aeroagri.model;

/** IMU: roll, pitch, yaw. The main reading is the tilt angle in degrees. */
public class IMUSensor extends Sensor {
    private double roll;
    private double pitch;
    private double yaw;

    public IMUSensor(int id, String name) {
        super(id, name);
    }

    @Override
    public double takeReading() {
        checkOnline();
        roll = round((random.nextDouble() - 0.5) * 2, 2);
        pitch = round((random.nextDouble() - 0.5) * 2, 2);
        yaw = round(90 + (random.nextDouble() - 0.5) * 2, 2);
        return round(Math.sqrt(roll * roll + pitch * pitch), 2); // tilt
    }

    @Override
    public String getUnit() { return "deg"; }

    @Override
    public String getType() { return "IMU"; }

    @Override
    public String getDetails() { return "roll=" + roll + ";pitch=" + pitch + ";yaw=" + yaw; }
}
