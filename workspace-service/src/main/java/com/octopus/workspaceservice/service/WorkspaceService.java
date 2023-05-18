package com.octopus.workspaceservice.service;

import com.octopus.dtomodels.Payload;
import com.octopus.dtomodels.UserDTO;
import com.octopus.dtomodels.WorkspaceDTO;
import com.octopus.workspaceservice.dtos.request.WorkspaceRequest;
import com.octopus.workspaceservice.models.Workspace;
import com.octopus.workspaceservice.models.WorkspaceMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceService {
//    public static final int WS_PER_PAGE = 4;
    public WorkspaceDTO createNewWorkspace(WorkspaceRequest workspaceRequest);

    public List<UserDTO> addMembers(String workspaceID, List<String> members, String token);
//    public void deleteWorkspace(UUID id);
//    public Workspace updateWorkspace(Workspace workSpace);
//    public Workspace searchWorkspace(String key);
//    public Optional<Workspace> findById(UUID id);
//    public List<Workspace> getAllWorkspace();
//
    public List<WorkspaceDTO> searchWorkspace(Payload payload);

}
