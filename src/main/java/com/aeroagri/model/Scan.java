package com.aeroagri.model;

import java.time.LocalDateTime;
import java.util.List;

/** One field scan: starts RUNNING, becomes COMPLETED when results are generated. */
public class Scan {
    private int id;
    private String fieldName;
    private String mode;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String overallStatus;
    private double avgHeight;
    private String warning;
    private int progress;
    private List<Sector> sectors;

    public Scan() { }   // used when reading from the database

    public Scan(String fieldName, String mode) {
        this.fieldName = fieldName;
        this.mode = mode;
        this.status = "RUNNING";
        this.startedAt = LocalDateTime.now();
        this.progress = 0;
    }

    /** Progress in % from elapsed time (the scan is simulated). */
    public void updateProgress(long elapsedMs, long totalMs) {
        progress = (int) Math.min(99, elapsedMs * 100 / Math.max(1, totalMs));
    }

    /** Same stages as the frontend animation. */
    public String getStage() {
        if (progress >= 100) return "RESULT";
        if (progress < 5) return "READY";
        if (progress < 15) return "TAKING OFF";
        if (progress < 40) return "FLYING";
        if (progress < 70) return "SCANNING";
        if (progress < 85) return "PROCESSING";
        return "ANALYZING";
    }

    /** Fills overall result from the sectors and marks the scan COMPLETED. */
    public void applyResults(List<Sector> sectorList) {
        this.sectors = sectorList;
        double sum = 0;
        int normal = 0;
        this.overallStatus = "NORMAL";
        this.warning = "";
        for (Sector s : sectorList) {
            if ("NORMAL".equals(s.getStatus())) {
                sum += s.getAvgHeightCm();
                normal++;
            }
        }
        this.avgHeight = normal == 0 ? 0 : Math.round(sum / normal);
        for (Sector s : sectorList) {
            if ("ATTENTION".equals(s.getStatus())) {
                this.overallStatus = "ATTENTION REQUIRED";
                this.warning = "LOW CROP GROWTH: Sector " + s.getName() + ", Row " + s.getProblemRow()
                        + " reads " + (int) s.getAvgHeightCm() + " cm, below the field average of "
                        + (int) avgHeight + " cm. Inspect Sector " + s.getName() + ", Row " + s.getProblemRow() + ".";
            }
        }
        this.status = "COMPLETED";
        this.completedAt = LocalDateTime.now();
        this.progress = 100;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }
    public double getAvgHeight() { return avgHeight; }
    public void setAvgHeight(double avgHeight) { this.avgHeight = avgHeight; }
    public String getWarning() { return warning; }
    public void setWarning(String warning) { this.warning = warning; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public List<Sector> getSectors() { return sectors; }
    public void setSectors(List<Sector> sectors) { this.sectors = sectors; }
}
