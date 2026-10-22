package com.monitoring.warehouse.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    @Bean
    public Queue measurementsQueue(MeasurementsProperties measurementsProperties) {
        return new Queue(measurementsProperties.queue());
    }
}
