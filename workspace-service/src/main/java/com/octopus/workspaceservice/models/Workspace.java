package com.octopus.workspaceservice.models;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.persistence.Column;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@Entity
@Table(name = "workspaces")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Workspace implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @javax.persistence.Column(name = "id",columnDefinition = "BINARY(16)")
    private UUID id;

    @javax.persistence.Column(name="name")
    private String name;

    @javax.persistence.Column(name="status")
    private boolean status = true;

    @javax.persistence.Column(name="avatar")
    private String avatar;

    @javax.persistence.Column(name = "created_date")
    @CreatedDate
    private Date createdDate;

    @javax.persistence.Column(name = "updated_date")
    @LastModifiedDate
    private Date updatedDate;

    @javax.persistence.Column(name="deleted_date")
    private Date deletedDate;

    @javax.persistence.Column(name = "created_by")
    private String createdBy;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    @ToString.Exclude
    private Set<WorkspaceMember> members = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    @ToString.Exclude
    private Set<Project> projects = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "workspace_id")
    @ToString.Exclude
    private Set<WorkspaceRole> workspaceRoles = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "workspace_id")
    @ToString.Exclude
    private Set<WorkspaceGroup> workspaceGroups = new HashSet<>();

    @ElementCollection(targetClass = ProjectRole.class, fetch = FetchType.LAZY)
    @JoinTable(name = "project_role", joinColumns = @JoinColumn(name = "workspace_id"))
    @Column(name = "project_role", nullable = false)
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    private Set<ProjectRole> projectRoles = new HashSet<>();

    public void addMember(WorkspaceMember member) {
        if (this.members == null)
            this.members = new HashSet<>();
        this.members.add(member);
    }

    public void addGroup(WorkspaceGroup group) {
        if (this.workspaceGroups == null)
            this.workspaceGroups = new HashSet<>();
        this.workspaceGroups.add(group);
    }

    public void addRole(WorkspaceRole role) {
        if (this.workspaceRoles == null)
            this.workspaceGroups = new HashSet<>();
        this.workspaceRoles.add(role);
    }

    public void addProjectRole(ProjectRole role) {
        if (this.projectRoles == null)
            this.projectRoles = new HashSet<>();
        this.projectRoles.add(role);
    }
}
