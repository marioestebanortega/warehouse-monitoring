package com.monitoring.warehouse.controller;

import com.monitoring.shared.model.Measurement;
import com.monitoring.shared.model.SensorType;
import com.monitoring.warehouse.LogCapture;
import com.monitoring.warehouse.config.UdpProperties;
import com.monitoring.warehouse.config.WarehouseProperties;
import com.monitoring.warehouse.service.MeasurementService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UdpSensorControllersTest {

    private final BlockingQueue<Measurement> published = new LinkedBlockingQueue<>();
    private final int temperaturePort = freePort();
    private final int humidityPort = freePort();
    private final UdpSensorControllers controllers = new UdpSensorControllers(
            new UdpProperties(Map.of("temperature", String.valueOf(temperaturePort), "humidity", String.valueOf(humidityPort))),
            new MeasurementService(published::add, new WarehouseProperties("warehouse-1")));

    @AfterEach
    void stopControllers() {
        controllers.stop();
    }

    @Test
    void listensOnePortPerSensorType() throws Exception {
        controllers.start();

        send(temperaturePort, "sensor_id=t1; value=30");
        assertEquals(new Measurement("warehouse-1", "t1", SensorType.TEMPERATURE, 30), published.poll(2, TimeUnit.SECONDS));
        send(humidityPort, "sensor_id=h1; value=40");
        assertEquals(new Measurement("warehouse-1", "h1", SensorType.HUMIDITY, 40), published.poll(2, TimeUnit.SECONDS));
    }

    @Test
    void logsEachListenerOnStart() {
        try (LogCapture logs = new LogCapture(UdpSensorControllers.class)) {
            controllers.start();

            assertEquals(List.of(
                    "INFO UDP_LISTENER_STARTED | type=TEMPERATURE; port=" + temperaturePort,
                    "INFO UDP_LISTENER_STARTED | type=HUMIDITY; port=" + humidityPort), logs.lines());
        }
    }

    @Test
    void reportsRunningUntilStopped() {
        controllers.start();
        assertTrue(controllers.isRunning());

        controllers.stop();
        assertFalse(controllers.isRunning());
    }

    private static void send(int port, String reading) throws IOException {
        byte[] data = reading.getBytes(StandardCharsets.UTF_8);
        try (DatagramSocket sensor = new DatagramSocket()) {
            sensor.send(new DatagramPacket(data, data.length, InetAddress.getLoopbackAddress(), port));
        }
    }

    private static int freePort() {
        try (DatagramSocket socket = new DatagramSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
