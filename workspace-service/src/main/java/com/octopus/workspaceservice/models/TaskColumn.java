package com.octopus.workspaceservice.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "task_column")
@IdClass(TaskColumnPK.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskColumn implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    @Id
    @ManyToOne
    @JoinColumn(name = "column_id")
    private Column column;

    @javax.persistence.Column(name = "replace_name")
    private String replaceName;

    @javax.persistence.Column(name = "value")
    private String value;
}
