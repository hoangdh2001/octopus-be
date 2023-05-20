package com.octopus.workspaceservice.service;

import com.octopus.dtomodels.Payload;
import com.octopus.dtomodels.UserDTO;
import com.octopus.dtomodels.WorkspaceDTO;
import com.octopus.workspaceservice.dtos.request.ProjectRequest;
import com.octopus.workspaceservice.dtos.request.WorkspaceRequest;
import com.octopus.workspaceservice.models.Workspace;
import com.octopus.workspaceservice.models.WorkspaceMember;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface WorkspaceService {
    public WorkspaceDTO createNewWorkspace(WorkspaceRequest workspaceRequest, String token);

    public Set<UserDTO> addMembers(String workspaceID, List<String> members, String token);
    public List<WorkspaceDTO> getAllWorkspace();
    public List<WorkspaceDTO> searchWorkspace(Payload payload, String token);

    public List<WorkspaceDTO> getWorkspaceByUser(String userID, String token);

    public WorkspaceDTO findWorkspaceByID(String id, String token);

    public WorkspaceDTO createProject(String workspaceID, ProjectRequest projectRequest, String token);

}
