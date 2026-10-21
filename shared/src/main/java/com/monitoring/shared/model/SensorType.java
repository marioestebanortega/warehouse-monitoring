package com.monitoring.shared.model;

import java.util.Locale;

public enum SensorType {
    TEMPERATURE,
    HUMIDITY;

    public String propertyKey() {
        return name().toLowerCase(Locale.ROOT);
    }
}
