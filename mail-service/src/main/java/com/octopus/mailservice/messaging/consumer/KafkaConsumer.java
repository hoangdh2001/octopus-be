package com.octopus.mailservice.messaging.consumer;

import com.octopus.dtomodels.Code;
import com.octopus.mailservice.service.SendMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final SendMailService mailService;

    @KafkaListener(
            topics = "mail.sendEmail"
            ,groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(Code code) {
        try {
            this.mailService.sendActivationEmail(code);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(
            topics = "mail.sendEmailForgotPass",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeForgotPassword(Code code) {
        try {
            this.mailService.sendResetPasswordEmail(code);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
