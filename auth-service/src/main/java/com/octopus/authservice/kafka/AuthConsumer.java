package com.octopus.authservice.kafka;

import com.octopus.authservice.dto.response.UserResponse;
import com.octopus.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthConsumer {

    private final UserService userService;

    @KafkaListener(topics = "user-get")
    public UserResponse findUser(int userID){
        return userService.findUserById(userID);
    }
}
