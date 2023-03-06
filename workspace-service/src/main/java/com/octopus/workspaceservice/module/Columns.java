package com.octopus.workspaceservice.module;

import lombok.*;

import javax.persistence.*;
import javax.persistence.Column;
import java.io.Serializable;

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
    private String createTime;

    @Column(name="update_time")
    private String updateTime;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;
}
