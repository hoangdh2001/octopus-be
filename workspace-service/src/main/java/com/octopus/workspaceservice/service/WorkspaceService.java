package com.octopus.workspaceservice.service;

import com.octopus.dtomodels.*;
import com.octopus.workspaceservice.dtos.request.AddSpaceRequest;
import com.octopus.workspaceservice.dtos.request.AddTaskRequest;
import com.octopus.workspaceservice.dtos.request.ProjectRequest;
import com.octopus.workspaceservice.dtos.request.WorkspaceRequest;

import java.util.List;
import java.util.Set;

public interface WorkspaceService {
    public WorkspaceDTO createNewWorkspace(WorkspaceRequest workspaceRequest, String token);

    public Set<UserDTO> addMembers(String workspaceID, List<String> members, String token);
    public List<WorkspaceDTO> getAllWorkspace();
    public List<WorkspaceDTO> searchWorkspace(Payload payload, String token);

    public List<WorkspaceDTO> getWorkspaceByUser(String userID, String token);

    public WorkspaceDTO findWorkspaceByID(String id, String token);

    public WorkspaceDTO createProject(String workspaceID, ProjectRequest projectRequest, String token);

    public ProjectDTO createSpace(String workspaceID, String projectID, AddSpaceRequest addSpaceRequest, String token);

    public ProjectDTO addTask(String projectID, String spaceID, AddTaskRequest taskRequest, String token);

}
