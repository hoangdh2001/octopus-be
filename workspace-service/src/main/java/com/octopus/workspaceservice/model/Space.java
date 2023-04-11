package com.octopus.workspaceservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "space")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Space implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="status")
    private boolean status;

    @Column(name="create_time")
    @CreatedDate
    private String createTime;

    @Column(name="update_time")
    @LastModifiedDate
    private String updateTime;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Columns> columns = new HashSet<>();

    @OneToMany(mappedBy = "spaceRoot", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Space> spaces = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space spaceRoot;

}
