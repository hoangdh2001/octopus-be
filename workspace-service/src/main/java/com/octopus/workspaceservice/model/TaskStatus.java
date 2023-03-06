package com.octopus.workspaceservice.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "task_status")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class TaskStatus implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="status")
    private int status;

    @Column(name="color")
    private String color;

    @Column(name="is_default")
    private boolean isDefault;

    @Column(name="create_time")
    private String createTime;

    @Column(name="update_time")
    private String updateTime;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
