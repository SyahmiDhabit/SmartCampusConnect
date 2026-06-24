package com.smartcampus.shared.messaging.config;

import com.smartcampus.shared.messaging.RabbitMqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange campusEventsExchange() {
        return new TopicExchange(RabbitMqConstants.EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitMqConstants.DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RabbitMqConstants.DLQ).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(RabbitMqConstants.DLQ);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(RabbitMqConstants.NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DLQ)
                .build();
    }

    @Bean
    public Queue reportingQueue() {
        return QueueBuilder.durable(RabbitMqConstants.REPORTING_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DLQ)
                .build();
    }

    @Bean
    public Binding notificationEnrolmentBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(campusEventsExchange())
                .with("enrolment.*");
    }

    @Bean
    public Binding notificationLibraryBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(campusEventsExchange())
                .with("library.#");
    }

    @Bean
    public Binding notificationStudentBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(campusEventsExchange())
                .with("student.*");
    }

    @Bean
    public Binding notificationLoadTestBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(campusEventsExchange())
                .with("loadtest.*");
    }

    @Bean
    public Binding reportingEnrolmentBinding() {
        return BindingBuilder.bind(reportingQueue())
                .to(campusEventsExchange())
                .with("enrolment.*");
    }

    @Bean
    public Binding reportingLibraryBinding() {
        return BindingBuilder.bind(reportingQueue())
                .to(campusEventsExchange())
                .with("library.#");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
