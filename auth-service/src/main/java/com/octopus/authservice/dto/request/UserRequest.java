package com.octopus.authservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String lastName;
    private String firstName;
    private String email;
    private String password;
    private String phoneNumber;

}
