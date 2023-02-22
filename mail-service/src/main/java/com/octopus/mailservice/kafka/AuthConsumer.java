package com.octopus.mailservice.kafka;

import com.octopus.authservice.dto.request.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

public class AuthConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topic.name}"
            ,groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(LoginRequest event){
        LOGGER.info(String.format("Auth event received in mail service => %s", event.toString()));

        // save the order event into the database
    }
}
