package com.octopus.authservice.dto.request;

import com.octopus.dtomodels.UserDTO;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeviceRequest {
    @NotNull
    @NotBlank
    private String deviceID;
    private String pushProvider;
}
