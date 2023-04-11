package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.model.WorkSpaceMember;

import java.util.Optional;

public interface WorkspaceMemberService {
    public static final int WSM_PER_PAGE = 4;
    public WorkSpaceMember addMember(WorkSpaceMember member);
    public void deleteMember(int id);
    public WorkSpaceMember updateMember(WorkSpaceMember member);
    public Optional<WorkSpaceMember> findMemberById(int id);
}
