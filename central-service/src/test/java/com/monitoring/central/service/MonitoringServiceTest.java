package com.monitoring.central.service;

import com.monitoring.central.LogCapture;
import com.monitoring.central.repository.ThresholdRepository;
import com.monitoring.shared.model.Measurement;
import com.monitoring.shared.model.SensorType;
import com.monitoring.shared.model.Threshold;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonitoringServiceTest {

    private static final Map<SensorType, Threshold> THRESHOLDS = Map.of(
            SensorType.TEMPERATURE, new Threshold(SensorType.TEMPERATURE, 35),
            SensorType.HUMIDITY, new Threshold(SensorType.HUMIDITY, 50));

    private final ThresholdRepository repository = THRESHOLDS::get;
    private final MonitoringService service = new MonitoringService(repository);

    @Test
    void logsAlarmWhenThresholdIsExceeded() {
        try (LogCapture logs = new LogCapture(MonitoringService.class)) {
            service.monitor(new Measurement("warehouse-1", "t1", SensorType.TEMPERATURE, 36));

            assertEquals(List.of("WARN ALARM | warehouse_id=warehouse-1; sensor_id=t1; type=TEMPERATURE; "
                    + "value=36.0°C; threshold=35.0°C"), logs.lines());
        }
    }

    @Test
    void logsHumidityAlarmWithPercentUnit() {
        try (LogCapture logs = new LogCapture(MonitoringService.class)) {
            service.monitor(new Measurement("warehouse-2", "h1", SensorType.HUMIDITY, 55));

            assertEquals(List.of("WARN ALARM | warehouse_id=warehouse-2; sensor_id=h1; type=HUMIDITY; "
                    + "value=55.0%; threshold=50.0%"), logs.lines());
        }
    }

    @Test
    void doesNotLogAlarmWhenThresholdIsNotExceeded() {
        try (LogCapture logs = new LogCapture(MonitoringService.class)) {
            service.monitor(measurement(SensorType.TEMPERATURE, 35));

            assertEquals(List.of(), logs.lines());
        }
    }

    private Measurement measurement(SensorType type, double value) {
        return new Measurement("warehouse-1", "s1", type, value);
    }
}
