package com.octopus.authservice.model;

import com.octopus.dtomodels.UserDTO;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "first_name", length = 64)
    private String firstName;

    @Column(name = "last_name", length = 64)
    private String lastName;

    @Column(name = "email", length = 128, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 64)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "gender")
    private Boolean gender;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "last_active")
    private Date lastActive;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "enabled", columnDefinition = "bit default 0")
    private Boolean enabled = false;

    @Column(name = "created_date")
    @CreatedDate
    private Date createdDate;

    @Column(name = "updated_date")
    @LastModifiedDate
    private Date updatedDate;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<Connection> connections = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private List<Device> devices;

    public User merge(User other) {
        this.firstName = other.getFirstName() != null ? other.getFirstName() : this.firstName;
        this.lastName = other.getLastName() != null ? other.getLastName() : this.lastName;
        this.email = other.getEmail() != null ? other.getEmail() : this.email;
        this.password = other.getPassword() != null ? other.getPassword() : this.password;
        this.phoneNumber = other.getPhoneNumber() != null ? other.getPhoneNumber() : this.phoneNumber;
        this.birthday = other.getBirthday() != null ? other.getBirthday() : this.birthday;
        this.gender = other.getGender() != null ? other.getGender() : this.gender;
        this.active = other.getActive() != null ? other.getActive() : this.active;
        this.lastActive = other.getLastActive() != null ? other.getLastActive() : this.lastActive;
        this.avatar = other.getAvatar() != null ? other.getAvatar() : this.avatar;
        this.refreshToken = other.getRefreshToken() != null ? other.getRefreshToken() : this.refreshToken;
        this.enabled = other.getEnabled() != null ? other.getEnabled() : this.enabled;
        this.createdDate = other.getCreatedDate() != null ? other.getCreatedDate() : this.createdDate;
        this.updatedDate = other.getUpdatedDate() != null ? other.getUpdatedDate() : this.updatedDate;
        if (other.getConnections() != null) {
            var count = this.connections.size();
            var otherCount = other.getConnections().size();
            if (count > otherCount) {
                this.connections.remove(count - 1);
            } else if (count < otherCount) {
                this.connections.add(other.getConnections().get(otherCount - 1));
            }
        }
        return this;
    }
}
