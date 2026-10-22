package com.monitoring.central.config;

import com.monitoring.shared.exception.ConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BrokerPropertiesTest {

    @Test
    void failsWhenPasswordIsMissingWithoutRevealingOtherValues() {
        ConfigurationException exception = assertThrows(ConfigurationException.class,
                () -> new BrokerProperties("rabbitmq", "5672", "user", null));
        assertEquals("Missing property 'spring.rabbitmq.password'", exception.getMessage());
    }

    @Test
    void textRepresentationHidesPassword() {
        BrokerProperties properties = new BrokerProperties("rabbitmq", "5672", "user", "secret");

        assertFalse(properties.toString().contains("secret"));
    }
}
