package com.aeroagri;

import static org.junit.jupiter.api.Assertions.*;

import com.aeroagri.model.Scan;
import com.aeroagri.service.CsvExporter;
import com.aeroagri.service.ScanService;
import com.aeroagri.service.SensorService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"aeroagri.scan-seconds=0", "spring.datasource.url=jdbc:h2:mem:testdb"})
class ScanServiceTest {
    @Autowired ScanService scanService;
    @Autowired SensorService sensorService;

    @BeforeEach
    void reset() {
        sensorService.setMode("healthy");
    }

    @Test
    void healthyScanCompletesAndIsSaved() {
        Scan started = scanService.start("Field A");
        Scan done = scanService.getWithResults(started.getId());
        assertEquals("COMPLETED", done.getStatus());
        assertEquals("NORMAL", done.getOverallStatus());
        assertEquals(4, scanService.getWithResults(started.getId()).getSectors().size());
    }

    @Test
    void problemScanWarnsAboutSectorB() {
        sensorService.setMode("problem");
        Scan done = scanService.getWithResults(scanService.start("Field A").getId());
        assertEquals("ATTENTION REQUIRED", done.getOverallStatus());
        assertTrue(done.getWarning().contains("Sector B, Row 4"));
    }

    @Test
    void readingsAreSavedInDatabase() {
        sensorService.readAll();
        assertEquals(3, sensorService.latest().size());
    }

    @Test
    void csvFileIsCreated(@TempDir Path dir) throws Exception {
        Scan scan = scanService.getWithResults(scanService.start("Field A").getId());
        Path file = new CsvExporter().export(scan, dir);
        List<String> lines = Files.readAllLines(file);
        assertEquals(5, lines.size()); // header + 4 sectors
    }
}
