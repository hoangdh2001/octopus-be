package com.octopus.workspaceservice.models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.persistence.Column;
import java.io.Serializable;
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
public class WorkspaceRole implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @javax.persistence.Column(name = "id",columnDefinition = "BINARY(16)")
    private UUID id;

    @javax.persistence.Column(name="name")
    private String name;

    @javax.persistence.Column(name="description")
    private String description;

    @javax.persistence.Column(name = "is_role_default")
    private boolean roleDefault;

    @ElementCollection(targetClass = WorkspaceOwnCapability.class, fetch = FetchType.LAZY)
    @JoinTable(name = "workspace_own_capabilities", joinColumns = @JoinColumn(name = "workspace_role_id"))
    @Column(name = "workspace_role_capabilities", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<WorkspaceOwnCapability> ownCapabilities = new HashSet<>();
}
