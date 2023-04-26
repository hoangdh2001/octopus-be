package com.octopus.dtomodels;

import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDTO {
    private String deviceID;
    private String pushProvider;
}
