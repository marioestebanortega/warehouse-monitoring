package com.monitoring.central;

import com.monitoring.central.controller.MeasurementController;
import com.monitoring.shared.exception.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.NestedExceptionUtils;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        "RABBITMQ_USERNAME=test",
        "RABBITMQ_PASSWORD=test",
        "spring.rabbitmq.listener.simple.auto-startup=false"
})
class CentralApplicationTest {

    @Autowired
    private MeasurementController measurementController;

    @Test
    void startsWithControllerWired() {
        assertNotNull(measurementController);
    }

    @Test
    void doesNotStartWithoutThreshold() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> new SpringApplicationBuilder(CentralApplication.class)
                .run("--RABBITMQ_USERNAME=test", "--RABBITMQ_PASSWORD=test", "--threshold.humidity=")
                .close());

        assertInstanceOf(ConfigurationException.class, NestedExceptionUtils.getMostSpecificCause(exception));
    }

}
