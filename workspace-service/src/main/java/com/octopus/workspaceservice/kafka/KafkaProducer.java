package com.octopus.workspaceservice.kafka;

import com.octopus.dtomodels.Code;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {
    private final KafkaTemplate<String, Code> template;

    public void sendEmailAddMemberWorkspace(Code code) {
        Message<Code> message = MessageBuilder
                .withPayload(code)
                .setHeader(KafkaHeaders.TOPIC, "mail.sendEmailAddMemberWorkspace")
                .build();
        template.send(message);
    }
}
