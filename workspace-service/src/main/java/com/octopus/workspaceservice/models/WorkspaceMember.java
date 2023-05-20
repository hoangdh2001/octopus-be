package com.octopus.workspaceservice.models;

import lombok.*;


import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "workspace_member")
@IdClass(WorkspaceMemberPK.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WorkspaceMember implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "workspace_id", columnDefinition = "BINARY(16)")
    private Workspace workspace;
    @Id
    @javax.persistence.Column(name = "member_id")
    private String memberID;
}
