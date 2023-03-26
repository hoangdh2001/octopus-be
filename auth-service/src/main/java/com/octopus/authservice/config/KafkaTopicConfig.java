package com.octopus.authservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic topic(){
        return TopicBuilder.name("mail.sendEmail")
                .build();
    }

    @Bean
    public NewTopic topicForgotPassword() {
        return TopicBuilder.name("mail.sendEmailForgotPass")
                .build();
    }
}
