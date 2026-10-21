package com.monitoring.shared.logging;

import com.monitoring.shared.model.Measurement;

import java.util.StringJoiner;

public final class LogMessage {

    private final LogEvent event;
    private final StringJoiner fields = new StringJoiner("; ");

    private LogMessage(LogEvent event) {
        this.event = event;
    }

    public static LogMessage of(LogEvent event) {
        return new LogMessage(event);
    }

    public LogMessage with(String key, Object value) {
        fields.add(key + "=" + escapeLineBreaks(String.valueOf(value)));
        return this;
    }

    public LogMessage withInput(String input) {
        return with("input", "\"" + input + "\"");
    }

    public LogMessage withException(Exception exception) {
        return with("reason", exception.getMessage()).with("exception", exception.getClass().getName());
    }

    public LogMessage withMeasurement(Measurement measurement) {
        return with("warehouse_id", measurement.warehouseId())
                .with("sensor_id", measurement.sensorId())
                .with("type", measurement.type())
                .with("value", measurement.value() + measurement.type().unit());
    }

    @Override
    public String toString() {
        return event + " | " + fields;
    }

    private static String escapeLineBreaks(String text) {
        return text.replace("\r", "\\r").replace("\n", "\\n");
    }
}
