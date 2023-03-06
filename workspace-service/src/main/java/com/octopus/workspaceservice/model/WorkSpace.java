package com.octopus.workspaceservice.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "work_space")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class WorkSpace implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name")
    private String name;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "workspace_roles", joinColumns = @JoinColumn(name = "workspace_id"), inverseJoinColumns = @JoinColumn(name = "role_workspace_id"))
    private Set<RoleWorkSpace> roleWorkspaces = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "workspace_member", joinColumns = @JoinColumn(name = "workspace_id"), inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<RoleWorkSpace> workspaceMembers = new HashSet<>();

    @OneToMany(mappedBy = "workSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Project> projects = new HashSet<>();
}
