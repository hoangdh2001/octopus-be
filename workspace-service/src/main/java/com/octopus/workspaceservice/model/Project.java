package com.octopus.workspaceservice.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "project")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Project implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="key")
    private String key;

    @Column(name="status")
    private boolean status;

    @Column(name="avatar")
    private String avatar;

    @Column(name="create_time")
    private String createTime;

    @Column(name="update_time")
    private String updateTime;

    @Column(name="delete_time")
    private String deleteTime;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Space> spaces = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "project_roles", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "role_project_id"))
    private Set<RoleWorkSpace> roleProjects = new HashSet<>();
}
