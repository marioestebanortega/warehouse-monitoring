package com.monitoring.warehouse.config;

import com.monitoring.shared.config.RequiredProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("warehouse")
public record WarehouseProperties(String id) {

    public WarehouseProperties {
        id = RequiredProperty.text("warehouse.id", id);
    }
}
