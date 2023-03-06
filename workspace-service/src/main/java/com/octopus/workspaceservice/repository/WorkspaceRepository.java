package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.model.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<WorkSpace, Integer> {
    public WorkSpace createWorkSpace(WorkSpace workSpace);
}
