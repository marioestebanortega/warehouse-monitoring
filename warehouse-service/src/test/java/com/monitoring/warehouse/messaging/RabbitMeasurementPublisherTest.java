package com.monitoring.warehouse.messaging;

import com.monitoring.shared.model.Measurement;
import com.monitoring.shared.model.SensorType;
import com.monitoring.warehouse.config.MeasurementsProperties;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RabbitMeasurementPublisherTest {

    @Test
    void sendsEncodedMeasurementToConfiguredQueue() {
        RecordingRabbitTemplate template = new RecordingRabbitTemplate();
        MeasurementPublisher publisher = new RabbitMeasurementPublisher(template, new MeasurementsProperties("measurements"));

        publisher.publish(new Measurement("warehouse-1", "t1", SensorType.TEMPERATURE, 30));

        assertEquals(List.of("measurements -> warehouse_id=warehouse-1; sensor_id=t1; type=TEMPERATURE; value=30.0"),
                template.sent);
    }

    private static final class RecordingRabbitTemplate extends RabbitTemplate {

        private final List<String> sent = new ArrayList<>();

        @Override
        public void convertAndSend(String routingKey, Object message) {
            sent.add(routingKey + " -> " + message);
        }
    }
}
