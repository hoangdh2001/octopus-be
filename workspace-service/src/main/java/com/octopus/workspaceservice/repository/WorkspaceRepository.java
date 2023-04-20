package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.models.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    //public WorkSpace createWorkSpace(WorkSpace workSpace);
//    @Query("SELECT ws FROM Workspace ws WHERE ws.name like '%?1' ")
//    public Workspace fillWorkspaceByKeywords(String key);
//
//    @Query("select ws from Workspace ws where ws.status = true")
//    public List<Workspace> findAllWorkspace();
}
