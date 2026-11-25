package com.aeroagri;

import static org.junit.jupiter.api.Assertions.*;

import com.aeroagri.exception.SensorException;
import com.aeroagri.model.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SensorTest {

    @Test
    void polymorphismAllSensorsGiveReadings() {
        List<Sensor> sensors = new ArrayList<>();
        sensors.add(new LiDARSensor(1, "L", 42.0));
        sensors.add(new GPSSensor(2, "G"));
        sensors.add(new IMUSensor(3, "I"));
        for (Sensor s : sensors) {
            assertNotNull(s.getType());
            assertNotNull(s.getUnit());
            s.takeReading();
        }
        assertEquals("LiDAR", sensors.get(0).getType());
    }

    @Test
    void lidarReadingIsNearBaseValue() {
        double v = new LiDARSensor(1, "L", 42.0).takeReading();
        assertTrue(v > 41 && v < 43);
    }

    @Test
    void offlineSensorThrowsCustomException() {
        LiDARSensor lidar = new LiDARSensor(1, "L", 42.0);
        lidar.setStatus("OFFLINE");
        assertThrows(SensorException.class, lidar::takeReading);
    }

    @Test
    void impossibleLidarValueThrowsException() {
        assertThrows(SensorException.class, () -> new LiDARSensor(1, "L", -50).takeReading());
    }

    @Test
    void problemModeHasSectorBRow4At21cm() {
        Sector b = Field.demoField("problem").getSectors().get(1);
        assertEquals("B", b.getName());
        assertEquals(21.0, b.getAvgHeightCm());
        assertEquals(4, b.getProblemRow());
        assertEquals("ATTENTION", b.getStatus());
    }

    @Test
    void healthyModeAllSectorsNormal() {
        for (Sector s : Field.demoField("healthy").getSectors()) {
            assertEquals("NORMAL", s.getStatus());
        }
    }
}
