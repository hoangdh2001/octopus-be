package com.octopus.workspaceservice.service;

import com.octopus.dtomodels.*;
import com.octopus.workspaceservice.dtos.request.*;

import java.util.List;
import java.util.Set;

public interface WorkspaceService {
    public WorkspaceDTO createNewWorkspace(WorkspaceRequest workspaceRequest, String token);

    public Set<UserDTO> addMembers(String workspaceID, AddMembersRequest members, String token);
    public List<WorkspaceDTO> getAllWorkspace();
    public List<WorkspaceDTO> searchWorkspace(Payload payload, String token);

    public List<WorkspaceDTO> getWorkspaceByUser(String userID, String token);

    public WorkspaceDTO findWorkspaceByID(String id, String token);

    public WorkspaceDTO createProject(String workspaceID, ProjectRequest projectRequest, String token);

    public ProjectDTO createSpace(String workspaceID, String projectID, AddSpaceRequest addSpaceRequest, String token);

    public ProjectDTO addTask(String projectID, String spaceID, AddTaskRequest taskRequest, String token);

    public ProjectDTO updateTask(TaskDTO taskDTO, String token);

    public GetTaskResponse getTaskToday(String workspaceID, String token);

    public GetTaskResponse getTaskExpirationDate(String workspaceID, String token);

    public GetTaskResponse getTaskNotStartDay(String workspaceID, String token);

    public GetTaskResponse getTaskDateInTerm(String workspaceID, String token);

    public GetTaskResponse getTaskNotDueDate(String workspaceID, String token);

    public GetTaskResponse getTaskDone(String workspaceID, String token);

    public ProjectDTO deleteTask(String projectID, String taskID, String token);

    public ProjectDTO deleteSpace(String projectID, String spaceID, String token);

}
