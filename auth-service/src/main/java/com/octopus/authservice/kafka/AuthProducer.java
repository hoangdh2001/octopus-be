package com.octopus.authservice.kafka;

import com.octopus.authservice.dto.request.LoginRequest;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Component
@EnableAutoConfiguration
public class AuthProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthProducer.class);

    private NewTopic topic;
    private KafkaTemplate<String, LoginRequest> kafkaTemplate;

    public AuthProducer(NewTopic topic, KafkaTemplate<String, LoginRequest> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(LoginRequest event){
        LOGGER.info(String.format("Auth event => %s %s", event.toString(), event.getEmail()));

        //HashMap<LoginRequest, String> h = new HashMap<LoginRequest, String>();
        //h.put(event, status);
        // create Message
        Message<LoginRequest> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        kafkaTemplate.send(message);
    }
}
