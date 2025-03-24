package com.internship.payment_service.rabbitmq.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConfig {

    /**
     * @return a Jackson2JsonMessageConverter for serializing/deserializing objects to/from JSON
     * messages sent over RabbitMQ
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Create a ConnectionFactory for RabbitMQ that caches connections.
     * <p>
     * The host name is "rabbitmq", and the username and password are "guest".
     * <p>
     * The caching connection factory is used to improve performance by reusing
     * connections when available.
     *
     * @return a ConnectionFactory for RabbitMQ
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("rabbitmq");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    /**
     * Creates a primary RabbitTemplate instance, which is an AmqpTemplate
     * implementation that sends messages to RabbitMQ.
     * <p>
     * The message converter is set to a Jackson2JsonMessageConverter, which
     * serializes/deserializes objects to/from JSON messages sent over RabbitMQ.
     *
     * @param connectionFactory a ConnectionFactory for RabbitMQ
     * @return a primary RabbitTemplate instance
     */
    @Bean
    @Primary
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
