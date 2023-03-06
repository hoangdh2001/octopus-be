package com.octopus.workspaceservice.module;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "task_column")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class TaskColumn implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="value")
    private String value;

    @Column(name="create_time")
    private String createTime;

    @Column(name="update_time")
    private String updateTime;
}
