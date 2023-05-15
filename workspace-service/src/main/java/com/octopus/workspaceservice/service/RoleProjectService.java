package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.models.RoleProject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleProjectService {
    public RoleProject addRoleProject(RoleProject roleProject);
    public RoleProject updateRoleProject(RoleProject roleProject);
    public void deleteRoleProject(UUID id);
    public Optional<RoleProject> findRoleProjectById(UUID id);
    public List<RoleProject> getAllRoleProject();
}
