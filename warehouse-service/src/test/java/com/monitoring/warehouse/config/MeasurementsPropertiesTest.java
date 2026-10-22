package com.monitoring.warehouse.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeasurementsPropertiesTest {

    @Test
    void readsQueueFromApplicationProperties() throws IOException {
        MutablePropertySources sources = new MutablePropertySources();
        sources.addLast(new ResourcePropertySource(new ClassPathResource("application.properties")));
        Binder binder = new Binder(ConfigurationPropertySources.from(sources));

        assertEquals("measurements", binder.bindOrCreate("measurements", MeasurementsProperties.class).queue());
    }
}
