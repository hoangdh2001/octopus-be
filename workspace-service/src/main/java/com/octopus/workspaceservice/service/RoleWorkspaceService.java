package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.model.RoleWorkSpace;

import java.util.List;
import java.util.Optional;

public interface RoleWorkspaceService {
    public RoleWorkSpace addRoleWorkspace(RoleWorkSpace roleWorkSpace);
    public RoleWorkSpace updateRoleWorkspace(RoleWorkSpace roleWorkSpace);
    public void deleteRoleWorkspace(int id);
    public Optional<RoleWorkSpace> findRoleWorkspaceById(int id);
    public List<RoleWorkSpace> getAllRoleWorkspace();
}
