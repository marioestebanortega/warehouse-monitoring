package com.monitoring.central.controller;

import com.monitoring.central.service.MonitoringService;
import com.monitoring.shared.exception.InvalidMeasurementException;
import com.monitoring.shared.logging.LogEvent;
import com.monitoring.shared.logging.LogMessage;
import com.monitoring.shared.messaging.MeasurementCodec;
import com.monitoring.shared.model.Measurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MeasurementController {

    private static final Logger LOG = LoggerFactory.getLogger(MeasurementController.class);

    private final MonitoringService monitoringService;

    public MeasurementController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @RabbitListener(queues = "${measurements.queue}")
    public void receive(String message) {
        try {
            Measurement measurement = MeasurementCodec.decode(message);
            LOG.info("{}", LogMessage.of(LogEvent.MEASUREMENT_RECEIVED).withMeasurement(measurement));
            monitoringService.monitor(measurement);
        } catch (InvalidMeasurementException exception) {
            LOG.warn("{}", LogMessage.of(LogEvent.INVALID_MESSAGE)
                    .with("reason", exception.getMessage())
                    .withInput(message));
        } catch (RuntimeException exception) {
            LOG.error("{}", LogMessage.of(LogEvent.UNEXPECTED_ERROR).withException(exception), exception);
        }
    }
}
