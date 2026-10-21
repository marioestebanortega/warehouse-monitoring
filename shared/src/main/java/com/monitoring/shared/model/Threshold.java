package com.monitoring.shared.model;

public record Threshold(SensorType type, double value) {

    public boolean isExceededBy(double measuredValue) {
        return measuredValue > value;
    }
}
