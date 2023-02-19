package com.octopus.authservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String lastName;
    private String firstName;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private Date birthDay;
    private boolean gender;
    private boolean active;
    private String avatar;
    private String refreshToken;
    private String verificationCode;
    private Date createTime;
    private Date updateTime;

}
