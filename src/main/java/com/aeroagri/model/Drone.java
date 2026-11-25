package com.aeroagri.model;

import java.util.ArrayList;
import java.util.List;

/** The drone owns a list of sensors. The list type is the parent class Sensor (polymorphism). */
public class Drone {
    private String name;
    private String status;
    private int battery;
    private List<Sensor> sensors = new ArrayList<>();

    public Drone(String name) {
        this(name, "Hovering / Idle", 100);
    }

    public Drone(String name, String status, int battery) {
        this.name = name;
        this.status = status;
        this.battery = battery;
    }

    public void addSensor(Sensor sensor) { sensors.add(sensor); }

    public String getName() { return name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getBattery() { return battery; }
    public void setBattery(int battery) { this.battery = battery; }
    public List<Sensor> getSensors() { return sensors; }
}
