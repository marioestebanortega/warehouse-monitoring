package com.monitoring.shared.model;

public record Measurement(String warehouseId, String sensorId, SensorType type, double value) {
}
