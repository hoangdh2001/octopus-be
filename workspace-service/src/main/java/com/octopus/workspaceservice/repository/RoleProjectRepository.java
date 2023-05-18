package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.models.Project;
import com.octopus.workspaceservice.models.RoleProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleProjectRepository extends JpaRepository<RoleProject, UUID> {
}
