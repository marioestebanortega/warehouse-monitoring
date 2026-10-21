package com.monitoring.central.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleLogPatternTest {

    @Test
    void consoleLineHasDateLevelServiceAndMessage() throws IOException {
        Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
        LoggerContext context = new LoggerContext();
        PatternLayout layout = new PatternLayout();
        layout.setContext(context);
        layout.setPattern(properties.getProperty("logging.pattern.console"));
        layout.start();
        LoggingEvent event = new LoggingEvent("test", context.getLogger("test"), Level.WARN, "ALARM | value=36.0°C", null, null);

        String line = layout.doLayout(event);

        assertTrue(line.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2} WARN  \\[central] ALARM \\| value=36\\.0°C\\R"), line);
        assertEquals("UTF-8", properties.getProperty("logging.charset.console"));
    }
}
