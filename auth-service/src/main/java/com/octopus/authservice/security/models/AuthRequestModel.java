package com.octopus.authservice.security.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class AuthRequestModel {
    @NotNull(message = "required")
    private String email;
    @Length(min = 4,max = 50)
    private String password;
}
