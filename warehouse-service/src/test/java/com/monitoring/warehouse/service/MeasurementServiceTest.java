package com.monitoring.warehouse.service;

import com.monitoring.shared.exception.InvalidMeasurementException;
import com.monitoring.shared.model.Measurement;
import com.monitoring.shared.model.SensorType;
import com.monitoring.warehouse.LogCapture;
import com.monitoring.warehouse.config.WarehouseProperties;
import com.monitoring.warehouse.messaging.MeasurementPublisher;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MeasurementServiceTest {

    private final List<Measurement> published = new ArrayList<>();
    private final MeasurementPublisher publisher = published::add;
    private final MeasurementService service = new MeasurementService(publisher, new WarehouseProperties("warehouse-1"));

    @Test
    void publishesTemperatureMeasurementWithWarehouseAndType() {
        service.process(SensorType.TEMPERATURE, "sensor_id=t1; value=30");

        assertEquals(List.of(new Measurement("warehouse-1", "t1", SensorType.TEMPERATURE, 30)), published);
    }

    @Test
    void logsPublishedMeasurement() {
        try (LogCapture logs = new LogCapture(MeasurementService.class)) {
            service.process(SensorType.TEMPERATURE, "sensor_id=t1; value=30");

            assertEquals(List.of("INFO MEASUREMENT_PUBLISHED | warehouse_id=warehouse-1; sensor_id=t1; "
                    + "type=TEMPERATURE; value=30.0°C"), logs.lines());
        }
    }

    @Test
    void invalidReadingIsNotPublished() {
        assertThrows(InvalidMeasurementException.class, () -> service.process(SensorType.TEMPERATURE, "sensor_id=t1"));

        assertEquals(List.of(), published);
    }
}
