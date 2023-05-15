package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.models.RoleWorkSpace;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleWorkspaceService {
    public RoleWorkSpace addRoleWorkspace(RoleWorkSpace roleWorkSpace);
    public RoleWorkSpace updateRoleWorkspace(RoleWorkSpace roleWorkSpace);
    public void deleteRoleWorkspace(UUID id);
    public Optional<RoleWorkSpace> findRoleWorkspaceById(UUID id);
    public List<RoleWorkSpace> getAllRoleWorkspace();
}
