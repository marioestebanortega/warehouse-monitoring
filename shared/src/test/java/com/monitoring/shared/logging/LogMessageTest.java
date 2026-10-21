package com.monitoring.shared.logging;

import com.monitoring.shared.model.Measurement;
import com.monitoring.shared.model.SensorType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogMessageTest {

    @Test
    void formatsEventAndFieldsInOrder() {
        String message = LogMessage.of(LogEvent.ALARM)
                .with("warehouse_id", "warehouse-1")
                .with("value", 36.0)
                .toString();

        assertEquals("ALARM | warehouse_id=warehouse-1; value=36.0", message);
    }

    @Test
    void describesMeasurementFieldsWithUnit() {
        String message = LogMessage.of(LogEvent.ALARM)
                .withMeasurement(new Measurement("warehouse-1", "h1", SensorType.HUMIDITY, 55))
                .toString();

        assertEquals("ALARM | warehouse_id=warehouse-1; sensor_id=h1; type=HUMIDITY; value=55.0%", message);
    }
}
