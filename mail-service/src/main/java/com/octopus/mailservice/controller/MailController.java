package com.octopus.mailservice.controller;

import com.octopus.authservice.dto.request.LoginRequest;
import com.octopus.authservice.service.UserService;
import com.octopus.mailservice.kafka.AuthConsumer;
import com.octopus.mailservice.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/api/mails")
@CrossOrigin
public class MailController {

    private UserService userService;

    public AuthConsumer authConsumer;

    private SendMailService mailService;

    /*public MailController(AuthConsumer authConsumer){
        this.authConsumer = authConsumer;
    }

    private MailController(UserService userService){
        this.userService = userService;
    }*/

    @GetMapping("/verifyMail/{status}")
    public void sendVerifyCodeToMail(LoginRequest loginRequest, @PathVariable String status){
        authConsumer.consume(loginRequest);
        loginRequest.getEmail();
        try {
            mailService.sendActivationEmail(loginRequest.getEmail(), status);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
