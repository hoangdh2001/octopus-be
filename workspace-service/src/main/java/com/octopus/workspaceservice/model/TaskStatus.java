package com.octopus.workspaceservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
    @CreatedDate
    private Date createTime;

    @Column(name="update_time")
    @LastModifiedDate
    private Date updateTime;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
