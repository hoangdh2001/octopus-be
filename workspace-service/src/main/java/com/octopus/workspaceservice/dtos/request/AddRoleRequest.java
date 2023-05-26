package com.octopus.workspaceservice.dtos.request;

import com.octopus.workspaceservice.models.WorkspaceOwnCapability;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddRoleRequest {
    private String name;
    private String description;
    private Set<WorkspaceOwnCapability> ownCapabilities;
}
