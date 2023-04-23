package com.octopus.workspaceservice.models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.persistence.Column;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "role_work_space")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class RoleWorkSpace implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @javax.persistence.Column(name = "id",columnDefinition = "BINARY(16)")
    private UUID id;

    @javax.persistence.Column(name="name")
    private String name;

    @javax.persistence.Column(name="description")
    private String description;

    @Column(name="user_id")
    private UUID userId;
}
