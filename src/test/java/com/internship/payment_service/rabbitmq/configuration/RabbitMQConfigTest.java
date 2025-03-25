package com.internship.payment_service.rabbitmq.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Primary;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQConfigTest {

    @InjectMocks
    private RabbitMQConfig rabbitMQConfig;

    @Test
    void jsonMessageConverter_shouldReturnJackson2JsonMessageConverter() {
        MessageConverter converter = rabbitMQConfig.jsonMessageConverter();

        assertNotNull(converter);
        assertInstanceOf(Jackson2JsonMessageConverter.class, converter);
    }

    @Test
    void connectionFactory_shouldCreateCachingConnectionFactoryWithCorrectSettings() {
        ConnectionFactory factory = rabbitMQConfig.connectionFactory();

        assertNotNull(factory);
        assertInstanceOf(CachingConnectionFactory.class, factory);

        CachingConnectionFactory cachingFactory = (CachingConnectionFactory) factory;
        assertEquals("rabbitmq", cachingFactory.getHost());
        assertEquals("guest", cachingFactory.getUsername());
    }

    @Test
    void amqpTemplate_shouldCreateRabbitTemplateWithJsonConverter() {
        ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);

        RabbitTemplate template = (RabbitTemplate) rabbitMQConfig.amqpTemplate(mockConnectionFactory);

        assertNotNull(template);
        assertEquals(mockConnectionFactory, template.getConnectionFactory());
        assertInstanceOf(Jackson2JsonMessageConverter.class, template.getMessageConverter());
    }

    @Test
    void amqpTemplate_shouldBePrimaryBean() throws NoSuchMethodException {
        assertNotNull(RabbitMQConfig.class
                .getMethod("amqpTemplate", ConnectionFactory.class)
                .getAnnotation(Primary.class));
    }

}