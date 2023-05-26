package com.octopus.workspaceservice.mapper;

import com.octopus.dtomodels.*;
import com.octopus.workspaceservice.models.*;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {
    @Mapping(target = "id", expression = "java(convertIdToString(workspace.getId()))")
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "projects", expression = "java(mapListProjectToProjectDTO(workspace.getProjects()))")
    @Mapping(target = "workspaceRoles", expression = "java(mapListWorkspaceRoleToWorkspaceRoleDTO(workspace.getWorkspaceRoles()))")
    @Mapping(target = "workspaceGroups", expression = "java(mapListWorkspaceGroupToWorkspaceGroupDTO(workspace.getWorkspaceGroups()))")
    WorkspaceDTO mapToWorkspaceDTO(Workspace workspace);

    @Named(value = "projects")
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "members", ignore = true)
    WorkspaceDTO mapToWorkspaceDTOIgnoreProject(Workspace workspace);

    @InheritConfiguration(name = "mapToWorkspaceDTO")
    List<WorkspaceDTO> mapListWorkspaceToWorkspaceDTO(List<Workspace> workspaces);

    @Mapping(target = "id", expression = "java(convertIdToString(project.getId()))")
    @Mapping(target = "spaces", expression = "java(mapListSpaceToSpaceDTO(project.getSpaces()))")
    @Mapping(target = "setting", expression = "java(mapToSettingDTO(project.getSetting()))")
    ProjectDTO mapToProjectDTO(Project project);

    @InheritConfiguration(name = "mapToProjectDTO")
    Set<ProjectDTO> mapListProjectToProjectDTO(Set<Project> projects);

    @Mapping(target = "id", expression = "java(convertIdToString(space.getId()))")
    @Mapping(target = "tasks", expression = "java(mapTaskListToTaskDTO(space.getTasks()))")
    @Mapping(target = "setting", expression = "java(mapToSettingDTO(space.getSetting()))")
    SpaceDTO mapToSpaceDTO(Space space);

    @InheritConfiguration(name = "mapToSpaceDTO")
    Set<SpaceDTO> mapListSpaceToSpaceDTO(Set<Space> spaces);

    @Mapping(target = "id", expression = "java(convertIdToString(task.getId()))")
    @Mapping(target = "taskStatus", expression = "java(mapToTaskStatusDTO(task.getTaskStatus()))")
    TaskDTO mapToTaskDTO(Task task);

    @InheritConfiguration(name = "mapToTaskDTO")
    Set<TaskDTO> mapTaskListToTaskDTO(Set<Task> tasks);

    @Mapping(target = "id", expression = "java(convertStringToId(taskDTO.getId()))")
    @Mapping(target = "taskStatus", expression = "java(mapTaskStatusDTOToTaskStatus(taskDTO.getTaskStatus()))")
    Task mapTaskDTOToTask(TaskDTO taskDTO);

    @Mapping(target = "id", expression = "java(convertIdToString(setting.getId()))")
    @Mapping(target = "statuses", expression = "java(mapTaskStatusListToTaskStatusDTO(setting.getTaskStatuses()))")
    SettingDTO mapToSettingDTO(Setting setting);

    @Mapping(target = "id", expression = "java(convertIdToString(taskStatus.getId()))")
    TaskStatusDTO mapToTaskStatusDTO(TaskStatus taskStatus);

    @InheritConfiguration(name = "mapToTaskStatusDTO")
    Set<TaskStatusDTO> mapTaskStatusListToTaskStatusDTO(Set<TaskStatus> taskStatuses);

    @Mapping(target = "id", expression = "java(convertStringToId(taskStatusDTO.getId()))")
    TaskStatus mapTaskStatusDTOToTaskStatus(TaskStatusDTO taskStatusDTO);

    @Mapping(target = "id", expression = "java(convertIdToString(workspaceRole.getId()))")
    @Mapping(target = "ownCapabilities", expression = "java(mapListWorkspaceOwnCapabilityToWorkspaceOwnCapabilityDTO(workspaceRole.getOwnCapabilities()))")
    WorkspaceRoleDTO mapToWorkspaceRoleDTO(WorkspaceRole workspaceRole);

    @InheritConfiguration(name = "mapToWorkspaceRoleDTO")
    Set<WorkspaceRoleDTO> mapListWorkspaceRoleToWorkspaceRoleDTO(Set<WorkspaceRole> workspaceRoles);

    WorkspaceOwnCapability mapToWorkspaceOwnCapability(WorkspaceOwnCapabilityDTO workspaceOwnCapabilityDTO);

    @InheritConfiguration(name = "mapToWorkspaceOwnCapability")
    Set<WorkspaceOwnCapabilityDTO> mapListWorkspaceOwnCapabilityToWorkspaceOwnCapabilityDTO(Set<WorkspaceOwnCapability> workspaceOwnCapabilities);

    @Mapping(target = "id", expression = "java(convertIdToString(workspaceGroup.getId()))")
    WorkspaceGroupDTO mapToWorkspaceGroupDTO(WorkspaceGroup workspaceGroup);

    @InheritConfiguration(name = "mapToWorkspaceGroupDTO")
    Set<WorkspaceGroupDTO> mapListWorkspaceGroupToWorkspaceGroupDTO(Set<WorkspaceGroup> workspaceGroups);

    default String convertIdToString(UUID id) {
        if (id == null) {
            return null;
        }
        return id.toString();
    }

    default UUID convertStringToId(String id) {
        if (id == null) {
            return null;
        }
        return UUID.fromString(id);
    }
}
