package com.octopus.workspaceservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "task")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="description")
    private String description;

    @Column(name="create_time")
    @CreatedDate
    private Date createTime;

    @Column(name="create_by")
    private String createBy;

    @Column(name="update_time")
    @LastModifiedDate
    private Date updateTime;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TaskStatus> taskStatuses = new HashSet<>();

    @OneToMany(mappedBy = "taskRoot", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task taskRoot;
}
