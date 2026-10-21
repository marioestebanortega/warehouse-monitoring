package com.monitoring.warehouse.controller;

import com.monitoring.shared.exception.InvalidMeasurementException;
import com.monitoring.shared.logging.LogEvent;
import com.monitoring.shared.logging.LogMessage;
import com.monitoring.shared.model.SensorType;
import com.monitoring.warehouse.service.MeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UdpSensorController implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(UdpSensorController.class);
    private static final int MAX_UDP_PAYLOAD = 65507;

    private final SensorType type;
    private final DatagramSocket socket;
    private final MeasurementService measurementService;

    public UdpSensorController(SensorType type, DatagramSocket socket, MeasurementService measurementService) {
        this.type = type;
        this.socket = socket;
        this.measurementService = measurementService;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[MAX_UDP_PAYLOAD];
        while (!socket.isClosed()) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                handle(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
            } catch (IOException exception) {
                if (!socket.isClosed()) {
                    logUnexpectedError(exception);
                }
            }
        }
    }

    private void handle(String reading) {
        try {
            measurementService.process(type, reading);
        } catch (InvalidMeasurementException exception) {
            LOG.warn("{}", LogMessage.of(LogEvent.INVALID_READING)
                    .with("reason", exception.getMessage())
                    .withInput(reading));
        } catch (RuntimeException exception) {
            logUnexpectedError(exception);
        }
    }

    private static void logUnexpectedError(Exception exception) {
        LOG.error("{}", LogMessage.of(LogEvent.UNEXPECTED_ERROR).withException(exception), exception);
    }
}
