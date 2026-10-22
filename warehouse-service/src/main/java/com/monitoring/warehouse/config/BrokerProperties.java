package com.monitoring.warehouse.config;

import com.monitoring.shared.config.RequiredProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.rabbitmq")
public record BrokerProperties(String host, String port, String username, String password) {

    public BrokerProperties {
        host = RequiredProperty.text("spring.rabbitmq.host", host);
        port = String.valueOf(RequiredProperty.port("spring.rabbitmq.port", port));
        username = RequiredProperty.text("spring.rabbitmq.username", username);
        password = RequiredProperty.text("spring.rabbitmq.password", password);
    }

    @Override
    public String toString() {
        return "BrokerProperties[host=" + host + ", port=" + port + ", username=" + username + "]";
    }
}
