package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.model.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkspaceRepository extends JpaRepository<WorkSpace, Integer> {
    //public WorkSpace createWorkSpace(WorkSpace workSpace);
    @Query("SELECT ws FROM WorkSpace ws WHERE ws.name like '%?1' ")
    public WorkSpace fillWorkspaceByKeywords(String key);
}
