package com.octopus.authservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "firstName", length = 64, nullable = false)
    private String firstName;

    @Column(name = "lastName", length = 64, nullable = false)
    private String lastName;

    @Column(name = "userName", length = 64)
    private String userName;

    @Column(name = "email", length = 128, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 64, nullable = false)
    private String password;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "birthDay")
    private Date birthDay;

    @Column(name = "sex")
    private boolean sex;

    @Column(name = "enable")
    private boolean enable;

    @Column(name = "photos")
    private String photos;

    @Column(name = "createTime")
    private Date createTime;

    @Column(name = "refreshToken")
    private String refreshToken;

    @Column(name = "verificationCode", length = 64)
    private String verificationCode;

    @Column(name="resetPasswordToken")
    private String resetPasswordToken;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

}
