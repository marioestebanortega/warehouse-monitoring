package com.monitoring.warehouse.controller;

import com.monitoring.shared.logging.LogEvent;
import com.monitoring.shared.logging.LogMessage;
import com.monitoring.shared.model.SensorType;
import com.monitoring.warehouse.config.UdpProperties;
import com.monitoring.warehouse.service.MeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

@Component
public class UdpSensorControllers implements SmartLifecycle {

    private static final Logger LOG = LoggerFactory.getLogger(UdpSensorControllers.class);

    private final UdpProperties udpProperties;
    private final MeasurementService measurementService;
    private final List<DatagramSocket> sockets = new ArrayList<>();

    public UdpSensorControllers(UdpProperties udpProperties, MeasurementService measurementService) {
        this.udpProperties = udpProperties;
        this.measurementService = measurementService;
    }

    @Override
    public void start() {
        for (SensorType type : SensorType.values()) {
            int port = udpProperties.portFor(type);
            DatagramSocket socket = open(port);
            sockets.add(socket);
            Thread.ofPlatform()
                    .name("udp-" + type.propertyKey())
                    .start(new UdpSensorController(type, socket, measurementService));
            LOG.info("{}", LogMessage.of(LogEvent.UDP_LISTENER_STARTED).with("type", type).with("port", port));
        }
    }

    @Override
    public void stop() {
        sockets.forEach(DatagramSocket::close);
        sockets.clear();
    }

    @Override
    public boolean isRunning() {
        return !sockets.isEmpty();
    }

    private static DatagramSocket open(int port) {
        try {
            return new DatagramSocket(port);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
