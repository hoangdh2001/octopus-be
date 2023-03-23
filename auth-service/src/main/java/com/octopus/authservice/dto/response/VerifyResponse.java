package com.octopus.authservice.dto.response;

import com.octopus.dtomodels.Code;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyResponse {
    private String email;
    private Boolean success;
    private Code.VerificationType verificationType;
}
