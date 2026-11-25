package com.aeroagri.service;

import com.aeroagri.exception.ScanNotFoundException;
import com.aeroagri.model.Field;
import com.aeroagri.model.Scan;
import com.aeroagri.repository.AeroRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Starts scans and completes them after a few seconds (no threads needed). */
@Service
public class ScanService {
    private final AeroRepository repo;
    private final SensorService sensorService;
    private final int scanSeconds;

    public ScanService(AeroRepository repo, SensorService sensorService,
                       @Value("${aeroagri.scan-seconds:10}") int scanSeconds) {
        this.repo = repo;
        this.sensorService = sensorService;
        this.scanSeconds = scanSeconds;
    }

    public Scan start(String fieldName) {
        Scan scan = new Scan(fieldName, sensorService.getMode());
        scan.setId(repo.insertScan(scan));
        return scan;
    }

    /** Returns the scan; if its time is over, the results are generated and saved now. */
    public synchronized Scan get(int id) {
        Scan scan = repo.findScan(id);
        if (scan == null) {
            throw new ScanNotFoundException(id);
        }
        if ("RUNNING".equals(scan.getStatus())) {
            long elapsed = Duration.between(scan.getStartedAt(), LocalDateTime.now()).toMillis();
            long total = scanSeconds * 1000L;
            if (elapsed >= total) {
                complete(scan);
            } else {
                scan.updateProgress(elapsed, total);
            }
        }
        return scan;
    }

    /** Scan with its sector-wise results (only meaningful when COMPLETED). */
    public Scan getWithResults(int id) {
        Scan scan = get(id);
        if ("COMPLETED".equals(scan.getStatus()) && scan.getSectors() == null) {
            scan.setSectors(repo.findResults(id));
        }
        return scan;
    }

    public List<Scan> history() {
        return repo.history();
    }

    private void complete(Scan scan) {
        scan.applyResults(Field.demoField(scan.getMode()).getSectors());
        sensorService.readAll();                         // save one last set of sensor readings
        repo.saveResults(scan.getId(), scan.getSectors());
        repo.completeScan(scan);
    }
}
