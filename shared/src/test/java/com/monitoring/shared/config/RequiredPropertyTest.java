package com.monitoring.shared.config;

import com.monitoring.shared.exception.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

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
}
