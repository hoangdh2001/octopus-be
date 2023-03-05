package com.octopus.mailservice.controller;

import com.octopus.authservice.dto.request.LoginRequest;
import com.octopus.authservice.service.UserService;
import com.octopus.mailservice.kafka.AuthConsumer;
import com.octopus.mailservice.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/api/mails")
//@CrossOrigin
public class MailController {

    private UserService userService;

    @Autowired
    public AuthConsumer authConsumer;

    @Autowired
    private SendMailService mailService;

    /*public MailController(AuthConsumer authConsumer){
        this.authConsumer = authConsumer;
    }

    private MailController(UserService userService){
        this.userService = userService;
    }*/

    @PostMapping("/verifyMail/{status}")
    @KafkaListener(
            topics = "${spring.kafka.topic.name}"
            ,groupId = "${spring.kafka.consumer.group-id}"
    )
    public void sendVerifyCodeToMail(@PathVariable String status){
        LoginRequest loginRequest = new LoginRequest();
        //loginRequest = authConsumer.consume();
        System.out.println(loginRequest.getEmail());
        System.out.println(authConsumer.consume(loginRequest).getEmail());
        //authConsumer.consume(loginRequest);
        //loginRequest.getEmail();

        try {
            //mailService.sendActivationEmail(loginRequest.getEmail(), status);
            mailService.sendActivationEmail(authConsumer.consume(loginRequest).getEmail(), status);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /*@KafkaListener(
            topics = "${spring.kafka.topic.name}"
            ,groupId = "${spring.kafka.consumer.group-id}"
    )
    private LoginRequest getLoginRequest(LoginRequest loginRequest){
        System.out.println(loginRequest.getEmail());
        return loginRequest;
    }*/
}
