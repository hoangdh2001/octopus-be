package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.models.RoleWorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleWorkspaceRepository extends JpaRepository<RoleWorkSpace, UUID> {
}
