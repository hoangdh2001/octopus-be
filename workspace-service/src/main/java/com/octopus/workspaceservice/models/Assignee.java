package com.octopus.workspaceservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Column;

@Entity(name = "assignees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AssigneePK.class)
public class Assignee {
    @Id
    @Column(name = "user_id")
    private String userID;
    @Id
    @ManyToOne
    @JoinColumn(name = "task_id", columnDefinition = "BINARY(16)")
    private Task task;
}
