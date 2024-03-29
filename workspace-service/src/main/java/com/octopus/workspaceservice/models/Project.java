package com.octopus.workspaceservice.models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.persistence.Column;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "projects")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
@ToString
public class Project implements Serializable {
    @Id
    @javax.persistence.Column(name = "id",columnDefinition = "BINARY(16)")
    private UUID id;

    @javax.persistence.Column(name="name")
    private String name;

    @javax.persistence.Column(name="status")
    private boolean status;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @ToString.Exclude
    private Set<Space> spaces = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setting_id")
    private Setting setting;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<ProjectMember> members = new HashSet<>();

    @javax.persistence.Column(name = "workspace_access")
    private Boolean workspaceAccess;

    public void addMember(ProjectMember member) {
        if (this.members == null)
            this.members = new HashSet<>();
        this.members.add(member);
    }
}
