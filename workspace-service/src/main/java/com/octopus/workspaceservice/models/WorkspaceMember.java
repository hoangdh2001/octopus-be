package com.octopus.workspaceservice.models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import javax.persistence.*;
import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "workspace_member")
@IdClass(WorkspaceMemberPK.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class WorkspaceMember implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "workspace_id", columnDefinition = "BINARY(16)")
    private Workspace workspace;
    @Id
    @javax.persistence.Column(name = "member_id")
    private String memberID;

    @javax.persistence.Column(name = "created_date")
    @CreatedDate
    private Date createdDate;

    @javax.persistence.Column(name = "updated_date")
    @LastModifiedDate
    private Date updatedDate;

    @javax.persistence.Column(name="workspace_role_id", columnDefinition = "binary(16)")
    private UUID workspaceRoleID;

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "member_groups", joinColumns = {@JoinColumn(name = "member_id", columnDefinition = "varchar(255)"), @JoinColumn(name = "workspace_id", columnDefinition = "BINARY(16)")})
    @Column(name = "`group`", nullable = false)
    @ToString.Exclude
    private Set<UUID> groups = new HashSet<>();

    public void addGroup(UUID id) {
        if (this.groups == null)
            this.groups = new HashSet<>();
        this.groups.add(id);
    }
}
