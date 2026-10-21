package com.monitoring.warehouse.messaging;

import com.monitoring.shared.model.Measurement;

public interface MeasurementPublisher {

    void publish(Measurement measurement);
}
