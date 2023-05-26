package com.octopus.workspaceservice.models;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.GenericGenerator;
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
@Table(name = "workspace_roles")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
public class WorkspaceRole implements Serializable {
    @Id
    @javax.persistence.Column(name = "id",columnDefinition = "BINARY(16)")
    private UUID id;

    @javax.persistence.Column(name="name")
    private String name;

    @javax.persistence.Column(name="description")
    private String description;

    @javax.persistence.Column(name = "is_role_default")
    private boolean roleDefault;

    @javax.persistence.Column(name = "created_date")
    @CreatedDate
    private Date createdDate;

    @javax.persistence.Column(name = "updated_date")
    @LastModifiedDate
    private Date updatedDate;

    @ElementCollection(targetClass = WorkspaceOwnCapability.class, fetch = FetchType.LAZY)
    @JoinTable(name = "workspace_own_capabilities", joinColumns = @JoinColumn(name = "workspace_role_id"))
    @Column(name = "workspace_role_capabilities", nullable = false)
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    private Set<WorkspaceOwnCapability> ownCapabilities = new HashSet<>();
}
