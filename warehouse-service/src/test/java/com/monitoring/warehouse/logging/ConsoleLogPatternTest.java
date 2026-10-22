package com.monitoring.warehouse.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleLogPatternTest {

    @Test
    void consoleLineHasDateLevelWarehouseIdAndMessage() throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("warehouse", Map.of("warehouse.id", "warehouse-1")));
        environment.getPropertySources().addLast(new ResourcePropertySource(new ClassPathResource("application.properties")));
        LoggerContext context = new LoggerContext();
        PatternLayout layout = new PatternLayout();
        layout.setContext(context);
        layout.setPattern(environment.getProperty("logging.pattern.console"));
        layout.start();
        LoggingEvent event = new LoggingEvent("test", context.getLogger("test"), Level.INFO, "MEASUREMENT_PUBLISHED | value=30.0", null, null);

        String line = layout.doLayout(event);

        assertTrue(line.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2} INFO  \\[warehouse-1] MEASUREMENT_PUBLISHED \\| value=30\\.0\\R"), line);
        assertEquals("UTF-8", environment.getProperty("logging.charset.console"));
    }
}
