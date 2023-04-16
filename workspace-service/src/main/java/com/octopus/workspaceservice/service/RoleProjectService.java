package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.model.RoleProject;

import java.util.List;
import java.util.Optional;

public interface RoleProjectService {
    public RoleProject addRoleProject(RoleProject roleProject);
    public RoleProject updateRoleProject(RoleProject roleProject);
    public void deleteRoleProject(int id);
    public Optional<RoleProject> findRoleProjectById(int id);
    public List<RoleProject> getAllRoleProject();
}
