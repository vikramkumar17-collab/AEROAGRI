package com.aeroagri.exception;

/** Custom exception: thrown when a scan id does not exist in the database. */
public class ScanNotFoundException extends RuntimeException {
    public ScanNotFoundException(int id) {
        super("Scan " + id + " not found");
    }
}
