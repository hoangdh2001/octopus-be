package com.octopus.authservice.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "devices")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Device implements Serializable {
    @Id
    @Column(name = "deviceID")
    private String deviceID;
    private String pushProvider;
    private String name;

    @Column(name = "created_at")
    @CreatedDate
    private Date createdAt;

    private boolean disabled;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
