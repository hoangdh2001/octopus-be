package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.models.Workspace;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceService {
    public static final int WS_PER_PAGE = 4;
    public Workspace createWorkspace(Workspace workSpace);
    public void deleteWorkspace(UUID id);
    public Workspace updateWorkspace(Workspace workSpace);
    public Workspace searchWorkspace(String key);
    public Optional<Workspace> findById(UUID id);
    public List<Workspace> getAllWorkspace();

}
