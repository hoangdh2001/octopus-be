package com.octopus.workspaceservice.mapper;

import com.octopus.dtomodels.ProjectDTO;
import com.octopus.dtomodels.WorkspaceDTO;
import com.octopus.workspaceservice.models.Project;
import com.octopus.workspaceservice.models.Workspace;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {
    @Mapping(target = "id", expression = "java(convertIdToString(workspace.getId()))")
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "projects", expression = "java(mapListProjectToProjectDTO(workspace.getProjects()))")
    WorkspaceDTO mapToWorkspaceDTO(Workspace workspace);

    @InheritConfiguration(name = "mapToWorkspaceDTO")
    List<WorkspaceDTO> mapListWorkspaceToWorkspaceDTO(List<Workspace> workspaces);

    @Mapping(target = "id", expression = "java(convertIdToString(project.getId()))")
    @Mapping(target = "spaces", ignore = true)
    ProjectDTO mapToProjectDTO(Project project);

    @InheritConfiguration(name = "mapToProjectDTO")
    Set<ProjectDTO> mapListProjectToProjectDTO(Set<Project> projects);

    default String convertIdToString(UUID id) {
        if (id == null) {
            return null;
        }
        return id.toString();
    }
}
