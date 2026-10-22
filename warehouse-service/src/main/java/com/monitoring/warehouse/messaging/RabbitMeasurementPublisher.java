package com.monitoring.warehouse.messaging;

import com.monitoring.shared.messaging.MeasurementCodec;
import com.monitoring.shared.model.Measurement;
import com.monitoring.warehouse.config.MeasurementsProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMeasurementPublisher implements MeasurementPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String queue;

    public RabbitMeasurementPublisher(RabbitTemplate rabbitTemplate, MeasurementsProperties measurementsProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = measurementsProperties.queue();
    }

    @Override
    public void publish(Measurement measurement) {
        rabbitTemplate.convertAndSend(queue, MeasurementCodec.encode(measurement));
    }
}
