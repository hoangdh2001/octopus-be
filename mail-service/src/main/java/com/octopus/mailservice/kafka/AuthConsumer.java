package com.octopus.mailservice.kafka;

import com.octopus.authservice.dto.request.LoginRequest;
import com.octopus.mailservice.service.SendMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.HashMap;

@Service
public class AuthConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthConsumer.class);
    @Autowired
    private SendMailService mailService;

    @KafkaListener(
            topics = "${spring.kafka.topic.name}"
            ,groupId = "${spring.kafka.consumer.group-id}"
    )
    public LoginRequest consume(LoginRequest event){
        LOGGER.info(String.format("Auth event received in mail service => %s %s %s", event.toString(), event.getEmail(), event.getOtp()));

        // save the auth event into the database
        try {
            this.mailService.sendActivationEmail(event.getEmail(), "login", event.getOtp());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println(event.getEmail());
        return event;
    }
}
