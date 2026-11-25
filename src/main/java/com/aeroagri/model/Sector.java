package com.aeroagri.model;

/** One part (A, B, C or D) of a field with its average crop height. */
public class Sector {
    public static final double MIN_NORMAL_CM = 30; // below this -> ATTENTION

    private String name;
    private double avgHeightCm;
    private int problemRow; // 0 = no problem row
    private String status;
    private String note;

    public Sector(String name, double avgHeightCm, int problemRow) {
        this.name = name;
        this.avgHeightCm = avgHeightCm;
        this.problemRow = problemRow;
        evaluate();
    }

    private void evaluate() {
        if (avgHeightCm < MIN_NORMAL_CM) {
            status = "ATTENTION";
            note = "Row " + problemRow + " Stunted Growth";
        } else {
            status = "NORMAL";
            note = "Canopy Uniform";
        }
    }

    public String getName() { return name; }
    public double getAvgHeightCm() { return avgHeightCm; }
    public void setAvgHeightCm(double avgHeightCm) { this.avgHeightCm = avgHeightCm; evaluate(); }
    public int getProblemRow() { return problemRow; }
    public String getStatus() { return status; }
    public String getNote() { return note; }
}
