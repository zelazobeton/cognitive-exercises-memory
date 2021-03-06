package com.zelazobeton.cognitiveexercisesmemory.services.rabbitMQ.impl;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zelazobeton.cognitiveexercisesmemory.domain.messages.AbstractQueueMessage;
import com.zelazobeton.cognitiveexercisesmemory.exception.MessagePublishException;
import com.zelazobeton.cognitiveexercisesmemory.services.rabbitMQ.MessagePublisherService;

@Service
public class MessagePublisherServiceImpl implements MessagePublisherService {

    @Value("${spring.rabbitmq.defaultExchange}")
    private String defaultExchangeName;
    private RabbitTemplate rabbitTemplate;


    private Map<String, List<String>> routings;

    public MessagePublisherServiceImpl(RabbitTemplate rabbitTemplate,
            @Value("#{${spring.rabbitmq.routings}}") Map<String, List<String>> routings) {
        this.rabbitTemplate = rabbitTemplate;
        this.routings = routings;
    }

    @Override
    @Transactional
    public void publishMessage(AbstractQueueMessage messageObject, String exchange) {
        try {
            List<String> routingKeys = this.routings.get(messageObject.key());
            if (routingKeys == null || routingKeys.isEmpty()) {
                throw new MessagePublishException(String.format(
                        "Error during sending the message to the exchange: %s - missing routing key for %s event",
                        exchange, messageObject.key()));
            }
            for (String routingKey : routingKeys) {
                this.rabbitTemplate.convertAndSend(exchange, routingKey, messageObject);
            }
        } catch (AmqpException exception) {
            throw new MessagePublishException(
                    String.format("Error during sending the message to the exchange: %s", exchange), exception);
        }
    }

    @Override
    @Transactional
    public void publishMessage(AbstractQueueMessage message) {
        this.publishMessage(message, this.defaultExchangeName);
    }
}
