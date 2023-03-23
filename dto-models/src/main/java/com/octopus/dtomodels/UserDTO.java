package com.octopus.dtomodels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO implements Serializable {
    private String id;
    @Email
    @NotBlank
    private String email;
    private String firstName;
    private String lastName;
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String password;
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
}
