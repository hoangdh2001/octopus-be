package com.octopus.authservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 64, nullable = false)
    private String lastName;

    @Column(name = "username", length = 64)
    private String username;

    @Column(name = "email", length = 128, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 64, nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "birth_day")
    private Date birthDay;

    @Column(name = "gender")
    private boolean gender;

    @Column(name = "active")
    private boolean active;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(name = "created_date")
    @CreatedDate
    private Date createdDate;

    @Column(name = "updated_date")
    @LastModifiedDate
    private Date updatedDate;
}
