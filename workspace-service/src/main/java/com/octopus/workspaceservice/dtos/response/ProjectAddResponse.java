package com.octopus.workspaceservice.dtos.response;

import com.octopus.dtomodels.ProjectDTO;
import com.octopus.dtomodels.WorkspaceDTO;
import com.octopus.workspaceservice.models.Project;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProjectAddResponse {
    private WorkspaceDTO workspace;
    private Project project;
}
