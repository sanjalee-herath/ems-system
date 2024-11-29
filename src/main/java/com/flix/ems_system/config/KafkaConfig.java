package com.flix.ems_system.config;

import com.flix.ems_system.event.EmployeeEvent;
import com.flix.ems_system.event.TaskEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {
    private final ProducerFactory<String, EmployeeEvent> employeeEventProducerFactory;
    private final ProducerFactory<String, TaskEvent> taskProducerFactory;
    
    public KafkaConfig(ProducerFactory<String, EmployeeEvent> producerFactory,
                       ProducerFactory<String, TaskEvent> taskProducerFactory) {
        this.employeeEventProducerFactory = producerFactory;
        this.taskProducerFactory = taskProducerFactory;
    }

    @Bean
    public KafkaTemplate<String, EmployeeEvent> kafkaEmployeeTemplate() {
        return new KafkaTemplate<>(employeeEventProducerFactory);
    }

    @Bean
    public KafkaTemplate<String, TaskEvent> kafkaTaskTemplate() {
        return new KafkaTemplate<>(taskProducerFactory);
    }
}
