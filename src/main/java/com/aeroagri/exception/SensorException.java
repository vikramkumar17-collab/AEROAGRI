package com.aeroagri.exception;

/** Custom exception: thrown when a sensor is offline or gives an impossible value. */
public class SensorException extends RuntimeException {
    public SensorException(String message) {
        super(message);
    }
}
