package com.monitoring.central.repository;

import com.monitoring.central.config.ThresholdProperties;
import com.monitoring.shared.model.SensorType;
import com.monitoring.shared.model.Threshold;
import org.springframework.stereotype.Repository;

@Repository
public class PropertiesThresholdRepository implements ThresholdRepository {

    private final ThresholdProperties properties;

    public PropertiesThresholdRepository(ThresholdProperties properties) {
        this.properties = properties;
    }

    @Override
    public Threshold findByType(SensorType type) {
        return new Threshold(type, properties.valueFor(type));
    }
}
