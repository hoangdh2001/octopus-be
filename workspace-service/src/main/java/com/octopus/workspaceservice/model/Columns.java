package com.octopus.workspaceservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "column")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Columns implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="status")
    private boolean status;

    @Column(name="create_time")
    @CreatedDate
    private Date createTime;

    @Column(name="update_time")
    @LastModifiedDate
    private Date updateTime;

    @Column(name="delete_time")
    @LastModifiedDate
    private Date deleteTime;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;
}
