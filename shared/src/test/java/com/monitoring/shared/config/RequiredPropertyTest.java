package com.monitoring.shared.config;

import com.monitoring.shared.exception.ConfigurationException;
import com.monitoring.shared.model.SensorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequiredPropertyTest {

    @Test
    void readsNumber() {
        assertEquals(35.5, RequiredProperty.number("threshold.temperature", " 35.5 "));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "  ")
    void failsWhenNumberIsMissing(String value) {
        ConfigurationException exception = assertThrows(ConfigurationException.class,
                () -> RequiredProperty.number("threshold.temperature", value));
        assertEquals("Missing property 'threshold.temperature'", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"hot", "NaN"})
    void failsWhenNumberIsInvalid(String value) {
        assertThrows(ConfigurationException.class, () -> RequiredProperty.number("threshold.temperature", value));
    }

    @Test
    void readsPort() {
        assertEquals(3344, RequiredProperty.port("udp.port.temperature", "3344"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "0", "65536"})
    void failsWhenPortIsInvalid(String value) {
        ConfigurationException exception = assertThrows(ConfigurationException.class,
                () -> RequiredProperty.port("udp.port.temperature", value));
        assertEquals("Invalid port '" + value + "' in property 'udp.port.temperature'", exception.getMessage());
    }

    @Test
    void readsOneValuePerSensorType() {
        Map<SensorType, Integer> ports = RequiredProperty.bySensorType("udp.port",
                Map.of("temperature", "3344", "humidity", "3355"), RequiredProperty::port);

        assertEquals(Map.of(SensorType.TEMPERATURE, 3344, SensorType.HUMIDITY, 3355), ports);
    }

    @Test
    void failsWhenSensorTypeValueIsMissing() {
        ConfigurationException exception = assertThrows(ConfigurationException.class,
                () -> RequiredProperty.bySensorType("udp.port", null, RequiredProperty::port));
        assertEquals("Missing property 'udp.port.temperature'", exception.getMessage());
    }
}
