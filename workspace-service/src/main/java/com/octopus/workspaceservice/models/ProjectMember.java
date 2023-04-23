package com.octopus.workspaceservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "project_member")
@IdClass(WorkspaceMemberPK.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectMember {
    @Id
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @Id
    @javax.persistence.Column(name = "member_id", columnDefinition = "BINARY(16)")
    private UUID memberID;
    @Id
    @ManyToOne
    @JoinColumn(name = "workspavemember_id")
    private WorkspaceMember workspaceMember;
}
