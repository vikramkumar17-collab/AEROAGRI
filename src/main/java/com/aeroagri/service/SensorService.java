package com.aeroagri.service;

import com.aeroagri.exception.SensorException;
import com.aeroagri.model.Drone;
import com.aeroagri.model.GPSSensor;
import com.aeroagri.model.IMUSensor;
import com.aeroagri.model.LiDARSensor;
import com.aeroagri.model.Sensor;
import com.aeroagri.repository.AeroRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Holds the drone, reads all sensors (simulated) and remembers the demo mode. */
@Service
public class SensorService {
    private final AeroRepository repo;
    private final Drone drone = new Drone("AEROAGRI-D1");
    private String mode = "healthy";

    public SensorService(AeroRepository repo) {
        this.repo = repo;
        drone.addSensor(new LiDARSensor(1, "LiDAR-01", 42.0));
        drone.addSensor(new GPSSensor(2, "GPS-01"));
        drone.addSensor(new IMUSensor(3, "IMU-01"));
    }

    public Drone getDrone() { return drone; }

    public synchronized String getMode() { return mode; }

    public synchronized void setMode(String newMode) {
        if (!"healthy".equals(newMode) && !"problem".equals(newMode)) {
            throw new IllegalArgumentException("mode must be 'healthy' or 'problem'");
        }
        this.mode = newMode;
    }

    /** Takes a fresh (simulated) reading from every sensor and saves it in the database. */
    public List<Map<String, Object>> readAll() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Sensor s : drone.getSensors()) {            // polymorphism: Sensor reference
            Map<String, Object> m = new HashMap<>();
            m.put("sensorId", s.getId());
            m.put("name", s.getName());
            m.put("type", s.getType());
            m.put("simulated", true);
            try {
                double value = s.takeReading();          // calls the child class version
                m.put("value", value);
                m.put("unit", s.getUnit());
                m.put("details", s.getDetails());
                m.put("status", s.getStatus());
                repo.saveReading(s.getId(), value, s.getDetails(), s.getUnit(), getMode());
            } catch (SensorException e) {
                m.put("status", "ERROR");
                m.put("error", e.getMessage());
            }
            result.add(m);
        }
        return result;
    }

    /** Latest saved readings; takes new ones if the database is still empty. */
    public List<Map<String, Object>> latest() {
        List<Map<String, Object>> list = repo.latestReadings();
        return list.isEmpty() ? readAll() : list;
    }

    public String summary() {
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> m : latest()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append(m.get("type")).append(" ").append(m.get("value")).append(" ").append(m.get("unit"));
        }
        return sb.append(" (SIMULATED)").toString();
    }
}
