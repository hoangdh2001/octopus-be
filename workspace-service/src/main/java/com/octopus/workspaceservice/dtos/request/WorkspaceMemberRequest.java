package com.octopus.workspaceservice.dtos.request;

import com.octopus.workspaceservice.models.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMemberRequest {
    private Workspace workspace;
    private UUID userId;
}
