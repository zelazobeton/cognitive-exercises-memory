package com.zelazobeton.cognitiveexercisesmemory.services.rabbitMQ;

import com.zelazobeton.cognitiveexercisesmemory.domain.messages.AbstractQueueMessage;

public interface MessagePublisherService {
    void publishMessage(AbstractQueueMessage message, String exchange);
    void publishMessage(AbstractQueueMessage message);
}
