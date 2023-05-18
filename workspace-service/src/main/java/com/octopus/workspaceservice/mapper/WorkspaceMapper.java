package com.octopus.workspaceservice.mapper;

import com.octopus.dtomodels.WorkspaceDTO;
import com.octopus.workspaceservice.models.Workspace;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {
    @Mapping(target = "id", expression = "java(convertIdToString(workspace.getId()))")
    WorkspaceDTO mapToWorkspaceDTO(Workspace workspace);

    @InheritConfiguration(name = "mapToWorkspaceDTO")
    List<WorkspaceDTO> mapListWorkspaceToWorkspaceDTO(List<Workspace> workspaces);

    default String convertIdToString(UUID id) {
        if (id == null) {
            return null;
        }
        return id.toString();
    }
}
