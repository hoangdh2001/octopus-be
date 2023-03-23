package com.octopus.authservice.dto.request;

import com.octopus.authservice.valid.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetRequest {
    @NotBlank
    private String code;
    @Email
    @NotBlank
    private String email;
    @Password
    private String password;
}
