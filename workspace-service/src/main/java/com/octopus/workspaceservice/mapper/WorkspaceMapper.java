package com.octopus.workspaceservice.mapper;

import com.octopus.dtomodels.*;
import com.octopus.workspaceservice.models.*;
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
    @Mapping(target = "assignees", expression = "java(mapAssigneeToString(task.getAssignees()))")
    TaskDTO mapToTaskDTO(Task task);

    @InheritConfiguration(name = "mapToTaskDTO")
    Set<TaskDTO> mapTaskListToTaskDTO(Set<Task> tasks);

    @Mapping(target = "id", expression = "java(convertIdToString(setting.getId()))")
    @Mapping(target = "statuses", expression = "java(mapTaskStatusListToTaskStatusDTO(setting.getTaskStatuses()))")
    SettingDTO mapToSettingDTO(Setting setting);

    @Mapping(target = "id", expression = "java(convertIdToString(taskStatus.getId()))")
    TaskStatusDTO mapToTaskStatusDTO(TaskStatus taskStatus);

    @InheritConfiguration(name = "mapToTaskStatusDTO")
    Set<TaskStatusDTO> mapTaskStatusListToTaskStatusDTO(Set<TaskStatus> taskStatuses);

    default String convertIdToString(UUID id) {
        if (id == null) {
            return null;
        }
        return id.toString();
    }

    default String convertAssigneeToString(Assignee assignee) {
        if (assignee == null) {
            return null;
        }
        return assignee.getUserID();
    }

    default List<String> mapAssigneeToString(Set<Assignee> assignees) {
        if (assignees == null)  {
            return null;
        }

        return assignees.stream().map(this::convertAssigneeToString).collect(java.util.stream.Collectors.toList());
    }
}
