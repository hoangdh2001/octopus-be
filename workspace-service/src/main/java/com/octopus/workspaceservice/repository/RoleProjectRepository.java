package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.models.Project;
import com.octopus.workspaceservice.models.RoleProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleProjectRepository extends JpaRepository<RoleProject, UUID> {
}
