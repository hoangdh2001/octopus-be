package com.octopus.authservice.security.models;

import com.octopus.dtomodels.Code;
import com.octopus.dtomodels.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {
    private String accessToken;
    private String tokenType;
    private Long expiredIn;
    private UserDTO userDTO;
    private Code.VerificationType verificationType;
}
