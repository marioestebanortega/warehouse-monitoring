package com.monitoring.warehouse.service;

import com.monitoring.shared.logging.LogEvent;
import com.monitoring.shared.logging.LogMessage;
import com.monitoring.shared.messaging.MeasurementCodec;
import com.monitoring.shared.model.Measurement;
import com.monitoring.shared.model.SensorReading;
import com.monitoring.shared.model.SensorType;
import com.monitoring.warehouse.config.WarehouseProperties;
import com.monitoring.warehouse.messaging.MeasurementPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MeasurementService {

    private static final Logger LOG = LoggerFactory.getLogger(MeasurementService.class);

    private final MeasurementPublisher publisher;
    private final String warehouseId;

    public MeasurementService(MeasurementPublisher publisher, WarehouseProperties warehouseProperties) {
        this.publisher = publisher;
        this.warehouseId = warehouseProperties.id();
    }

    public void process(SensorType type, String rawReading) {
        SensorReading reading = MeasurementCodec.decodeReading(rawReading);
        Measurement measurement = new Measurement(warehouseId, reading.sensorId(), type, reading.value());
        publisher.publish(measurement);
        LOG.info("{}", LogMessage.of(LogEvent.MEASUREMENT_PUBLISHED).withMeasurement(measurement));
    }
}
