package com.octopus.workspaceservice.kafka;

import com.octopus.authservice.dto.request.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class AuthConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topic.name}"
            ,groupId = "${spring.kafka.consumer.group-id}"
    )
    public LoginRequest consume(LoginRequest event){
        LOGGER.info(String.format("Auth event received in mail service => %s %s %s", event.toString(), event.getEmail(), event.getOtp()));

        return event;
    }
}
