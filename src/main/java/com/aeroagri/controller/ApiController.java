package com.aeroagri.controller;

import com.aeroagri.exception.ScanNotFoundException;
import com.aeroagri.model.Field;
import com.aeroagri.model.Scan;
import com.aeroagri.model.User;
import com.aeroagri.service.CsvExporter;
import com.aeroagri.service.ScanService;
import com.aeroagri.service.SensorService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** REST API used by the AEROAGRI frontend. */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {
    private final SensorService sensorService;
    private final ScanService scanService;
    private final User user = new User(1, "Field Operator", "field.operator@aeroagri.farm", "OPERATOR");

    public ApiController(SensorService sensorService, ScanService scanService) {
        this.sensorService = sensorService;
        this.scanService = scanService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        Field field = Field.demoField(sensorService.getMode());
        Map<String, Object> drone = new HashMap<>();
        drone.put("name", sensorService.getDrone().getName());
        drone.put("status", sensorService.getDrone().getStatus());
        drone.put("battery", sensorService.getDrone().getBattery());

        Map<String, Object> fieldInfo = new HashMap<>();
        fieldInfo.put("name", field.getName());
        fieldInfo.put("crop", field.getCrop());
        fieldInfo.put("areaAcres", field.getAreaAcres());
        fieldInfo.put("sectorCount", field.getSectors().size());

        Map<String, Object> d = new HashMap<>();
        d.put("dataSource", "SIMULATED");
        d.put("mode", sensorService.getMode());
        d.put("user", user);
        d.put("drone", drone);
        d.put("field", fieldInfo);
        d.put("latestReadings", sensorService.latest());
        return d;
    }

    @GetMapping("/sensors")
    public List<Map<String, Object>> sensors() {
        return sensorService.readAll();
    }

    @GetMapping("/sensors/latest")
    public List<Map<String, Object>> latestSensors() {
        return sensorService.latest();
    }

    @PostMapping("/demo/mode")
    public Map<String, String> setMode(@RequestBody Map<String, String> body) {
        sensorService.setMode(body.get("mode"));
        return Map.of("mode", sensorService.getMode());
    }

    @GetMapping("/fields")
    public List<Field> fields() {
        return List.of(Field.demoField(sensorService.getMode()));
    }

    @PostMapping("/scans/start")
    public Scan startScan(@RequestBody(required = false) Map<String, String> body) {
        String fieldName = (body != null && body.get("field") != null) ? body.get("field") : "Field A";
        return scanService.start(fieldName);
    }

    @GetMapping("/scans/history")
    public List<Scan> history() {
        return scanService.history();
    }

    @GetMapping("/scans/{id}")
    public Scan getScan(@PathVariable int id) {
        return scanService.get(id);
    }

    @GetMapping("/scans/{id}/results")
    public Map<String, Object> results(@PathVariable int id) {
        Scan scan = scanService.getWithResults(id);
        if (!"COMPLETED".equals(scan.getStatus())) {
            throw new IllegalStateException("Scan " + id + " is still running");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("scan", scan);
        m.put("sectors", scan.getSectors());
        m.put("sensorSummary", sensorService.summary());
        m.put("dataSource", "SIMULATED");
        return m;
    }

    @GetMapping("/reports/scans/{id}/csv")
    public ResponseEntity<byte[]> csv(@PathVariable int id) throws IOException {
        Scan scan = scanService.getWithResults(id);
        if (!"COMPLETED".equals(scan.getStatus())) {
            throw new IllegalStateException("Scan " + id + " is still running");
        }
        Path file = new CsvExporter().export(scan, Path.of("reports"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scan_" + id + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(Files.readAllBytes(file));
    }

    // ---------- error handling ----------
    @ExceptionHandler(ScanNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(ScanNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> notReady(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
}
