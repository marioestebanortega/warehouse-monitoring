package com.monitoring.shared.logging;

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
}
