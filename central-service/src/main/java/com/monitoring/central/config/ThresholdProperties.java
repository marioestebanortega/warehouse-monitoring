package com.monitoring.central.config;

import com.monitoring.shared.config.RequiredProperty;
import com.monitoring.shared.model.SensorType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties
public class ThresholdProperties {

    private final Map<SensorType, Double> values;

    public ThresholdProperties(Map<String, String> threshold) {
        values = RequiredProperty.bySensorType("threshold", threshold, RequiredProperty::number);
    }

    public double valueFor(SensorType type) {
        return values.get(type);
    }
}
