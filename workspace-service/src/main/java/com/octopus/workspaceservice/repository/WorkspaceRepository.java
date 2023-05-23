package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.models.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID>, JpaSpecificationExecutor<Workspace> {
    //public WorkSpace createWorkSpace(WorkSpace workSpace);
//    @Query("SELECT ws FROM Workspace ws WHERE ws.name like '%?1' ")
//    public Workspace fillWorkspaceByKeywords(String key);
//
    @Query("select ws from Workspace ws where ws.status = true")
    public List<Workspace> findAllWorkspace();

    @Query("select ws from Workspace ws join ws.members wm where wm.memberID = :userID")
    public List<Workspace> findWorkspacesByMemberID(@Param("userID") String userID);

    Workspace findWorkspaceById(UUID id);

}
