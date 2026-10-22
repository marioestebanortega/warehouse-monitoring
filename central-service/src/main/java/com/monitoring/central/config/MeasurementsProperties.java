package com.monitoring.central.config;

import com.monitoring.shared.config.RequiredProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("measurements")
public record MeasurementsProperties(String queue) {

    public MeasurementsProperties {
        queue = RequiredProperty.text("measurements.queue", queue);
    }
}
