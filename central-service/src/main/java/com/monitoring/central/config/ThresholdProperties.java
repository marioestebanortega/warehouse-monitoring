package com.monitoring.central.config;

import com.monitoring.shared.config.RequiredProperty;
import com.monitoring.shared.model.SensorType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.EnumMap;
import java.util.Map;

@ConfigurationProperties
public class ThresholdProperties {

    private static final String PREFIX = "threshold.";

    private final Map<SensorType, Double> values = new EnumMap<>(SensorType.class);

    public ThresholdProperties(Map<String, String> threshold) {
        Map<String, String> configured = threshold == null ? Map.of() : threshold;
        for (SensorType type : SensorType.values()) {
            String key = type.propertyKey();
            values.put(type, RequiredProperty.number(PREFIX + key, configured.get(key)));
        }
    }

    public double valueFor(SensorType type) {
        return values.get(type);
    }
}
