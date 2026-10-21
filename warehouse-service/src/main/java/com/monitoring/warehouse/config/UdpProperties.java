package com.monitoring.warehouse.config;

import com.monitoring.shared.config.RequiredProperty;
import com.monitoring.shared.model.SensorType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("udp")
public class UdpProperties {

    private final Map<SensorType, Integer> ports;

    public UdpProperties(Map<String, String> port) {
        ports = RequiredProperty.bySensorType("udp.port", port, RequiredProperty::port);
    }

    public int portFor(SensorType type) {
        return ports.get(type);
    }
}
