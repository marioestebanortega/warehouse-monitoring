package com.monitoring.warehouse.config;

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

class UdpPropertiesTest {

    @Test
    void readsPortsFromApplicationProperties() throws IOException {
        UdpProperties properties = bind(Map.of());

        assertEquals(3344, properties.portFor(SensorType.TEMPERATURE));
        assertEquals(3355, properties.portFor(SensorType.HUMIDITY));
    }

    @Test
    void failsWhenPortIsMissing() {
        ConfigurationException exception = assertThrows(ConfigurationException.class,
                () -> new UdpProperties(Map.of("temperature", "3344")));
        assertEquals("Missing property 'udp.port.humidity'", exception.getMessage());
    }

    private UdpProperties bind(Map<String, Object> environment) throws IOException {
        MutablePropertySources sources = new MutablePropertySources();
        sources.addLast(new SystemEnvironmentPropertySource("environment", environment));
        sources.addLast(new ResourcePropertySource(new ClassPathResource("application.properties")));
        Binder binder = new Binder(ConfigurationPropertySources.from(sources));
        return binder.bindOrCreate("udp", UdpProperties.class);
    }
}
