package com.octopus.workspaceservice.models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Workspace implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
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
    @LastModifiedDate
    private Date deletedDate;

    @OneToMany(mappedBy = "workspace",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkspaceMember> workspaceMembers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "workspace_id")
    private List<Project> projects;
}
