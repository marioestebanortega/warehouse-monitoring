package com.monitoring.shared.logging;

public enum LogEvent {
    ALARM,
    MEASUREMENT_PUBLISHED,
    INVALID_READING,
    UNEXPECTED_ERROR,
    UDP_LISTENER_STARTED,
    MEASUREMENT_RECEIVED,
    INVALID_MESSAGE
}
