package com.aeroagri.model;

import com.aeroagri.exception.SensorException;
import java.util.Random;

/** Abstract parent class of all sensors (abstraction + inheritance). */
public abstract class Sensor {
    // Encapsulation: private data, accessed through getters/setters
    private int id;
    private String name;
    private String status;

    protected final Random random = new Random();

    public Sensor(int id, String name) {
        this(id, name, "ONLINE");
    }

    public Sensor(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Abstract methods: every child MUST override these
    public abstract double takeReading();
    public abstract String getUnit();
    public abstract String getType();

    // Normal method: children may override it
    public String getDetails() {
        return "";
    }

    public String describe() {
        return getType() + " sensor " + name + " [" + status + "] (simulated)";
    }

    protected void checkOnline() {
        if ("OFFLINE".equals(status)) {
            throw new SensorException(name + " is offline");
        }
    }

    protected double round(double value, int places) {
        double f = Math.pow(10, places);
        return Math.round(value * f) / f;
    }
}
