package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.models.WorkspaceMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberService {
    public static final int WSM_PER_PAGE = 4;
    public WorkspaceMember addMember(WorkspaceMember member);
    public void deleteMember(UUID id);
    public WorkspaceMember updateMember(WorkspaceMember member);
    public Optional<WorkspaceMember> findMemberById(UUID id);
    public List<WorkspaceMember> getAllWorkspaceMember();

}
