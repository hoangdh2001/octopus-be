package com.octopus.authservice.dto.request;

import com.octopus.authservice.valid.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotNull
    @NotBlank
    private String currentPassword;
    @Password
    private String newPass;
}
