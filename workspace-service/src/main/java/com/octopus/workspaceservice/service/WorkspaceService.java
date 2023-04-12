package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.model.Project;
import com.octopus.workspaceservice.model.WorkSpace;
import com.octopus.workspaceservice.model.WorkSpaceMember;

import java.util.List;
import java.util.Optional;

public interface WorkspaceService {
    public static final int WS_PER_PAGE = 4;
    public WorkSpace createWorkspace(WorkSpace workSpace);
    public void deleteWorkspace(int id);
    public WorkSpace updateWorkspace(WorkSpace workSpace);
    public WorkSpace searchWorkspace(String key);
    public Optional<WorkSpace> findById(int id);
    public List<WorkSpace> getAllWorkspace();

}
