package com.octopus.dtomodels;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTaskResponse {
    private Set<TaskDTO> tasks;
    private WorkspaceDTO workspace;
}
