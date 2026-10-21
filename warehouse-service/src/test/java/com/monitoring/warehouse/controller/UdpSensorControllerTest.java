package com.monitoring.warehouse.controller;

import com.monitoring.shared.model.Measurement;
import com.monitoring.shared.model.SensorType;
import com.monitoring.warehouse.LogCapture;
import com.monitoring.warehouse.config.WarehouseProperties;
import com.monitoring.warehouse.service.MeasurementService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UdpSensorControllerTest {

    private final BlockingQueue<Measurement> published = new LinkedBlockingQueue<>();
    private final AtomicBoolean failNextPublish = new AtomicBoolean();
    private DatagramSocket socket;
    private Thread controllerThread;

    @BeforeEach
    void startController() throws IOException {
        socket = new DatagramSocket(0, InetAddress.getLoopbackAddress());
        MeasurementService service = new MeasurementService(this::publish, new WarehouseProperties("warehouse-1"));
        controllerThread = Thread.ofPlatform().start(new UdpSensorController(SensorType.TEMPERATURE, socket, service));
    }

    @AfterEach
    void stopController() throws InterruptedException {
        socket.close();
        controllerThread.join(TimeUnit.SECONDS.toMillis(2));
    }

    @Test
    void stopsWhenSocketIsClosed() throws InterruptedException {
        socket.close();
        controllerThread.join(TimeUnit.SECONDS.toMillis(2));

        assertFalse(controllerThread.isAlive());
    }

    @Test
    void logsInvalidReadingAndKeepsListening() throws Exception {
        try (LogCapture logs = new LogCapture(UdpSensorController.class)) {
            send("sensor_id=t1");
            send("sensor_id=t1; value=31");

            assertEquals(new Measurement("warehouse-1", "t1", SensorType.TEMPERATURE, 31), nextPublished());
            assertEquals("WARN INVALID_READING | reason=Missing field 'value'; input=\"sensor_id=t1\"",
                    logs.lines().getFirst());
        }
    }

    @Test
    void logsUnexpectedErrorAndKeepsListening() throws Exception {
        failNextPublish.set(true);
        try (LogCapture logs = new LogCapture(UdpSensorController.class)) {
            send("sensor_id=t1; value=30");
            send("sensor_id=t1; value=32");

            assertEquals(new Measurement("warehouse-1", "t1", SensorType.TEMPERATURE, 32), nextPublished());
            assertTrue(logs.lines().getFirst().startsWith(
                    "ERROR UNEXPECTED_ERROR | reason=Broker down; exception=java.lang.IllegalStateException"));
        }
    }

    private void publish(Measurement measurement) {
        if (failNextPublish.getAndSet(false)) {
            throw new IllegalStateException("Broker down");
        }
        published.add(measurement);
    }

    private void send(String reading) throws IOException {
        byte[] data = reading.getBytes(StandardCharsets.UTF_8);
        try (DatagramSocket sensor = new DatagramSocket()) {
            sensor.send(new DatagramPacket(data, data.length, socket.getLocalSocketAddress()));
        }
    }

    private Measurement nextPublished() throws InterruptedException {
        return published.poll(2, TimeUnit.SECONDS);
    }
}
