package com.monitoring.central.service;

import com.monitoring.central.repository.ThresholdRepository;
import com.monitoring.shared.logging.LogEvent;
import com.monitoring.shared.logging.LogMessage;
import com.monitoring.shared.model.Alarm;
import com.monitoring.shared.model.Measurement;
import com.monitoring.shared.model.Threshold;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MonitoringService {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoringService.class);

    private final ThresholdRepository thresholdRepository;

    public MonitoringService(ThresholdRepository thresholdRepository) {
        this.thresholdRepository = thresholdRepository;
    }

    public void monitor(Measurement measurement) {
        evaluate(measurement).ifPresent(MonitoringService::logAlarm);
    }

    private Optional<Alarm> evaluate(Measurement measurement) {
        Threshold threshold = thresholdRepository.findByType(measurement.type());
        if (threshold.isExceededBy(measurement.value())) {
            return Optional.of(new Alarm(measurement, threshold));
        }
        return Optional.empty();
    }

    private static void logAlarm(Alarm alarm) {
        Measurement measurement = alarm.measurement();
        LOG.warn("{}", LogMessage.of(LogEvent.ALARM)
                .withMeasurement(measurement)
                .with("threshold", alarm.threshold().value() + measurement.type().unit()));
    }
}
