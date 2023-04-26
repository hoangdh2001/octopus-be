package com.octopus.dtomodels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnUserDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Date birthday;
    private Boolean gender;
    private Boolean active;
    private Date lastActive;
    private String avatar;
    private String refreshToken;
    private Boolean enabled;
    private Date createdDate;
    private Date updatedDate;
    private List<String> connections;
    private List<DeviceDTO> devices;
}
