package com.octopus.workspaceservice.kafka;

import com.octopus.dtomodels.Code;
import com.octopus.workspaceservice.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final WorkspaceService workspaceService;

    /*@KafkaListener(
            topics = "workspace"
            ,groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(int id) {
        try {

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }*/
}
