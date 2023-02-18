package com.octopus.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {

    private int id;

    private String lastName;

    private String firstName;

    private String email;

    private String phoneNumber;

}
