package com.octopus.dtomodels;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceRoleDTO {
    private String id;
    private String name;
    private String description;
    private boolean roleDefault;
    private Set<WorkspaceOwnCapabilityDTO> ownCapabilities;
}
