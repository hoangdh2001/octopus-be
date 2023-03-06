package com.octopus.workspaceservice.module;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "role_project")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class RoleProject implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="user_id")
    private int userId;

}
