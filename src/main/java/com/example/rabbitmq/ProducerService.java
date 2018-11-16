package com.example.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    static final String EXISTENT_EXCHANGE = "ExistentExchange";
    private static final String NON_EXISTENT_EXCHANGE = "nonExistentExchange";
    private static final String QUEUE_NAME = "nonExistentQueue";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RabbitTemplate rabbitTemplate;

    public ProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback(this::confirmCallback);
    }

    private void confirmCallback(CorrelationData correlationData, boolean ack, String cause) {
        logger.info("In confirm callback, ack={}, cause={}, correlationData={}", ack, cause, correlationData);
        if (!ack) {
           sendMessage("resend Message", EXISTENT_EXCHANGE);
        } else {
            logger.info("sending was acknowledged");
        }
    }

    public void produceMessage() {
        sendMessage("initial Message", NON_EXISTENT_EXCHANGE);
    }

    private void sendMessage(String messageBody, String exchangeName) {
        logger.info("sending `{}`", messageBody);
        rabbitTemplate.send(exchangeName, QUEUE_NAME, new Message(messageBody.getBytes(), new MessageProperties()));
        logger.info("done with sending message");
    }

}
