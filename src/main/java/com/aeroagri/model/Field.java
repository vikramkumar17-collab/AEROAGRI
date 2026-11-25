package com.aeroagri.model;

import java.util.ArrayList;
import java.util.List;

/** A farm field made of sectors. */
public class Field {
    private int id;
    private String name;
    private String crop;
    private double areaAcres;
    private List<Sector> sectors = new ArrayList<>();

    public Field(int id, String name, String crop, double areaAcres) {
        this.id = id;
        this.name = name;
        this.crop = crop;
        this.areaAcres = areaAcres;
    }

    /** Builds the demo field. In "problem" mode Sector B, Row 4 is only 21 cm. */
    public static Field demoField(String mode) {
        Field f = new Field(1, "Field A", "Maize (Kharif)", 4.5);
        f.sectors.add(new Sector("A", 35, 0));
        if ("problem".equals(mode)) {
            f.sectors.add(new Sector("B", 21, 4));
        } else {
            f.sectors.add(new Sector("B", 34, 0));
        }
        f.sectors.add(new Sector("C", 36, 0));
        f.sectors.add(new Sector("D", 35, 0));
        return f;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCrop() { return crop; }
    public double getAreaAcres() { return areaAcres; }
    public List<Sector> getSectors() { return sectors; }
}
