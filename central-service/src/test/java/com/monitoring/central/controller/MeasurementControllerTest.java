package com.monitoring.central.controller;

import com.monitoring.central.LogCapture;
import com.monitoring.central.repository.ThresholdRepository;
import com.monitoring.central.service.MonitoringService;
import com.monitoring.shared.model.Threshold;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeasurementControllerTest {

    private static final ThresholdRepository THRESHOLDS = type -> new Threshold(type, 35);

    @Test
    void logsReceivedMeasurementAndMonitorsIt() {
        MeasurementController controller = new MeasurementController(new MonitoringService(THRESHOLDS));
        try (LogCapture controllerLogs = new LogCapture(MeasurementController.class);
             LogCapture monitoringLogs = new LogCapture(MonitoringService.class)) {
            controller.receive("warehouse_id=warehouse-1; sensor_id=t1; type=TEMPERATURE; value=36.0");

            assertEquals(List.of("INFO MEASUREMENT_RECEIVED | warehouse_id=warehouse-1; sensor_id=t1; "
                    + "type=TEMPERATURE; value=36.0°C"), controllerLogs.lines());
            assertEquals(1, monitoringLogs.lines().size());
        }
    }

    @Test
    void logsInvalidMessageWithoutMonitoringIt() {
        MeasurementController controller = new MeasurementController(new MonitoringService(THRESHOLDS));
        try (LogCapture controllerLogs = new LogCapture(MeasurementController.class);
             LogCapture monitoringLogs = new LogCapture(MonitoringService.class)) {
            controller.receive("warehouse_id=warehouse-1; sensor_id=t1; type=TEMPERATURE");

            assertEquals(List.of("WARN INVALID_MESSAGE | reason=Missing field 'value'; "
                    + "input=\"warehouse_id=warehouse-1; sensor_id=t1; type=TEMPERATURE\""), controllerLogs.lines());
            assertEquals(List.of(), monitoringLogs.lines());
        }
    }

    @Test
    void logsUnexpectedError() {
        ThresholdRepository failingRepository = type -> {
            throw new IllegalStateException("Thresholds unavailable");
        };
        MeasurementController controller = new MeasurementController(new MonitoringService(failingRepository));
        try (LogCapture controllerLogs = new LogCapture(MeasurementController.class)) {
            controller.receive("warehouse_id=warehouse-1; sensor_id=t1; type=TEMPERATURE; value=36.0");

            assertTrue(controllerLogs.lines().getLast().startsWith("ERROR UNEXPECTED_ERROR | "
                    + "reason=Thresholds unavailable; exception=java.lang.IllegalStateException"));
        }
    }
}
