package com.octopus.workspaceservice.model;

import lombok.*;


import javax.persistence.*;
import javax.persistence.Column;
import java.io.Serializable;

@Entity
@Table(name = "work_space_member")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class WorkSpaceMember implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="description")
    private String description;

    @Column(name="user_id")
    private String userId;
}
