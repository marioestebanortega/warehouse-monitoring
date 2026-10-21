package com.monitoring.shared.model;

import java.util.Locale;

public enum SensorType {
    TEMPERATURE("°C"),
    HUMIDITY("%");

    private final String unit;

    SensorType(String unit) {
        this.unit = unit;
    }

    public String unit() {
        return unit;
    }

    public String propertyKey() {
        return name().toLowerCase(Locale.ROOT);
    }
}
