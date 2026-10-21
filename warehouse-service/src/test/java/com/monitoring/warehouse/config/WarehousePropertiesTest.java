package com.monitoring.warehouse.config;

import com.monitoring.shared.exception.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WarehousePropertiesTest {

    @Test
    void readsWarehouseIdFromEnvironmentVariable() {
        MutablePropertySources sources = new MutablePropertySources();
        sources.addLast(new SystemEnvironmentPropertySource("environment", Map.of("WAREHOUSE_ID", "warehouse-1")));
        Binder binder = new Binder(ConfigurationPropertySources.from(sources));

        WarehouseProperties properties = binder.bindOrCreate("warehouse", WarehouseProperties.class);

        assertEquals("warehouse-1", properties.id());
    }

    @Test
    void failsWhenWarehouseIdIsMissing() {
        ConfigurationException exception = assertThrows(ConfigurationException.class,
                () -> new WarehouseProperties(null));
        assertEquals("Missing property 'warehouse.id'", exception.getMessage());
    }
}
