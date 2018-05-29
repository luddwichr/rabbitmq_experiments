package com.example.rabbitmq;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RabbitAutoConfiguration.class, ProducerService.class})
@DirtiesContext
public class ProducerServiceTest {

    @Autowired
    private ProducerService service;
    @SpyBean
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;

    @Before
    public void setup() {
        cachingConnectionFactory.setPublisherConfirms(true);
        amqpAdmin.declareExchange(new DirectExchange(ProducerService.EXISTENT_EXCHANGE));
    }

    @After
    public void cleanup() {
        amqpAdmin.deleteExchange(ProducerService.EXISTENT_EXCHANGE);
    }

    @Test
    public void sendMessageToNonexistentExchange() throws InterruptedException {
        final CountDownLatch sentMessagesLatch = new CountDownLatch(2);
        final List<Message> sentMessages = new ArrayList<>();
        doAnswer(invocation -> {
            invocation.callRealMethod();
            sentMessages.add(invocation.getArgument(2));
            sentMessagesLatch.countDown();
            return null;
        }).when(rabbitTemplate).send(anyString(), anyString(), any(Message.class));

//        service.runInSeparateThread = true;
        service.produceMessage();
        sentMessagesLatch.await();

        List<String> messageBodies = sentMessages.stream().map(message -> new String(message.getBody())).collect(toList());
        assertThat(messageBodies, equalTo(Arrays.asList("initial Message", "resend Message")));
    }

}