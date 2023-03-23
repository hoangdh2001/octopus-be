package com.octopus.authservice.messaging.producer;

import com.octopus.dtomodels.Code;
import com.octopus.dtomodels.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {

    private final NewTopic topic;

    private final NewTopic topicForgotPassword;

    private final KafkaTemplate<String, Code> template;

    public void sendEmail(Code code) {
        Message<Code> message = MessageBuilder
                .withPayload(code)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        template.send(message);
    }

    public void sendEmailForgotPassword(Code code) {
        Message<Code> message = MessageBuilder
                .withPayload(code)
                .setHeader(KafkaHeaders.TOPIC, topicForgotPassword.name())
                .build();
        template.send(message);
    }
}
