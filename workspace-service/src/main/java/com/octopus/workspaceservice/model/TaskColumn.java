package com.octopus.workspaceservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
    @CreatedDate
    private Date createTime;

    @Column(name="update_time")
    @LastModifiedDate
    private Date updateTime;
}
