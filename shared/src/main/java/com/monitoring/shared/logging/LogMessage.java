package com.monitoring.shared.logging;

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
        fields.add(key + "=" + value);
        return this;
    }

    @Override
    public String toString() {
        return event + " | " + fields;
    }
}
