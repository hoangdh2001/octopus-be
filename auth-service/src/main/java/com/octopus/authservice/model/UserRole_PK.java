package com.octopus.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRole_PK implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer user;
    private Integer role;
}
