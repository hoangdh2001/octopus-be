package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.model.WorkSpaceMember;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberService {
    public static final int PM_PER_PAGE = 4;
    public WorkSpaceMember addMember(WorkSpaceMember member);
    public void deleteMember(int id);
    public WorkSpaceMember updateMember(WorkSpaceMember member);
    public Optional<WorkSpaceMember> findMemberById(int id);
    public List<WorkSpaceMember> getAllWorkspaceMember();
}
