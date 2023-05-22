package com.octopus.workspaceservice.models;

import lombok.*;

import javax.persistence.*;
import javax.persistence.Column;

@Entity(name = "assignees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AssigneePK.class)
@Builder
public class Assignee {
    @Id
    @Column(name = "user_id")
    private String userID;
    @Id
    @ManyToOne
    @JoinColumn(name = "task_id", columnDefinition = "BINARY(16)")
    private Task task;
}
