package com.aeroagri.service;

import com.aeroagri.model.Scan;
import com.aeroagri.model.Sector;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/** File handling: writes scan results into a CSV file. */
public class CsvExporter {

    public Path export(Scan scan, Path folder) {
        try {
            Files.createDirectories(folder);
            Path file = folder.resolve("scan_" + scan.getId() + ".csv");
            try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
                out.println("scan_id,field,mode,overall_status,sector,avg_height_cm,status,note,data_source");
                for (Sector s : scan.getSectors()) {
                    out.println(scan.getId() + "," + scan.getFieldName() + "," + scan.getMode() + ","
                            + scan.getOverallStatus() + "," + s.getName() + "," + s.getAvgHeightCm() + ","
                            + s.getStatus() + "," + s.getNote() + ",SIMULATED");
                }
            }
            return file;
        } catch (IOException e) {
            throw new IllegalStateException("Could not write CSV file: " + e.getMessage());
        }
    }
}
