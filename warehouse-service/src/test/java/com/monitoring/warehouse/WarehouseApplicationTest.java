package com.monitoring.warehouse;

import com.monitoring.shared.exception.ConfigurationException;
import com.monitoring.warehouse.controller.UdpSensorControllers;
import com.monitoring.warehouse.messaging.MeasurementPublisher;
import com.monitoring.warehouse.messaging.RabbitMeasurementPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.DatagramSocket;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "warehouse.id=warehouse-test",
        "RABBITMQ_USERNAME=test",
        "RABBITMQ_PASSWORD=test"
})
class WarehouseApplicationTest {

    @Autowired
    private UdpSensorControllers udpSensorControllers;

    @Autowired
    private MeasurementPublisher measurementPublisher;

    @DynamicPropertySource
    static void freeUdpPorts(DynamicPropertyRegistry registry) {
        registry.add("udp.port.temperature", WarehouseApplicationTest::freePort);
        registry.add("udp.port.humidity", WarehouseApplicationTest::freePort);
    }

    @Test
    void startsListeningWithRabbitPublisher() {
        assertTrue(udpSensorControllers.isRunning());
        assertInstanceOf(RabbitMeasurementPublisher.class, measurementPublisher);
    }

    @Test
    void doesNotStartWithoutWarehouseId() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> new SpringApplicationBuilder(WarehouseApplication.class)
                .run("--RABBITMQ_USERNAME=test", "--RABBITMQ_PASSWORD=test", "--udp.port.temperature=" + freePort(),
                        "--udp.port.humidity=" + freePort())
                .close());

        assertInstanceOf(ConfigurationException.class, NestedExceptionUtils.getMostSpecificCause(exception));
    }

    private static int freePort() {
        try (DatagramSocket socket = new DatagramSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
