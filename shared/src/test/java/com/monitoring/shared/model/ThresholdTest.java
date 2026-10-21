package com.monitoring.shared.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThresholdTest {

    private final Threshold threshold = new Threshold(SensorType.TEMPERATURE, 35);

    @Test
    void valueAboveThresholdExceedsIt() {
        assertTrue(threshold.isExceededBy(35.1));
    }

    @Test
    void valueEqualToThresholdDoesNotExceedIt() {
        assertFalse(threshold.isExceededBy(35));
    }

    @Test
    void valueBelowThresholdDoesNotExceedIt() {
        assertFalse(threshold.isExceededBy(34));
    }
}
