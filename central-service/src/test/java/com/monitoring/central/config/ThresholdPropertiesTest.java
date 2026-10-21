package com.monitoring.central.config;

import com.monitoring.shared.exception.ConfigurationException;
import com.monitoring.shared.model.SensorType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThresholdPropertiesTest {

    @Test
    void readsThresholdsFromApplicationProperties() throws IOException {
        ThresholdProperties properties = bind(Map.of());

        assertEquals(35.0, properties.valueFor(SensorType.TEMPERATURE));
        assertEquals(50.0, properties.valueFor(SensorType.HUMIDITY));
    }

    @Test
    void environmentVariableOverridesApplicationProperties() throws IOException {
        ThresholdProperties properties = bind(Map.of("THRESHOLD_TEMPERATURE", "40"));

        assertEquals(40.0, properties.valueFor(SensorType.TEMPERATURE));
    }

    @Test
    void failsWhenThresholdIsMissing() {
        ConfigurationException exception = assertThrows(ConfigurationException.class,
                () -> new ThresholdProperties(Map.of("temperature", "35")));
        assertEquals("Missing property 'threshold.humidity'", exception.getMessage());
    }

    @Test
    void failsWhenNoThresholdIsConfigured() {
        assertThrows(ConfigurationException.class, () -> new ThresholdProperties(null));
    }

    private ThresholdProperties bind(Map<String, Object> environment) throws IOException {
        MutablePropertySources sources = new MutablePropertySources();
        sources.addLast(new SystemEnvironmentPropertySource("environment", environment));
        sources.addLast(new ResourcePropertySource(new ClassPathResource("application.properties")));
        Binder binder = new Binder(ConfigurationPropertySources.from(sources));
        return binder.bindOrCreate("", ThresholdProperties.class);
    }
}
