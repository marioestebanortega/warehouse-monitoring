package com.monitoring.central.repository;

import com.monitoring.central.config.ThresholdProperties;
import com.monitoring.shared.model.SensorType;
import com.monitoring.shared.model.Threshold;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesThresholdRepositoryTest {

    private final ThresholdRepository repository = new PropertiesThresholdRepository(
            new ThresholdProperties(Map.of("temperature", "35", "humidity", "50")));

    @Test
    void findsTemperatureThreshold() {
        assertEquals(new Threshold(SensorType.TEMPERATURE, 35), repository.findByType(SensorType.TEMPERATURE));
    }
}
