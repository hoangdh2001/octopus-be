package com.octopus.workspaceservice.dtos.request;

import com.octopus.workspaceservice.models.Project;
import com.octopus.workspaceservice.models.WorkspaceMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberRequest {
    private Project project;
    private UUID userId;
    private WorkspaceMember workspaceMember;
}
