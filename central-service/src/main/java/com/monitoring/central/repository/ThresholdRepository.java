package com.monitoring.central.repository;

import com.monitoring.shared.model.SensorType;
import com.monitoring.shared.model.Threshold;

public interface ThresholdRepository {

    Threshold findByType(SensorType type);
}
