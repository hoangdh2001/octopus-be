package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.models.ProjectMember;
import com.octopus.workspaceservice.models.WorkspaceMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberService {
    public static final int PM_PER_PAGE = 4;
    public ProjectMember addMember(ProjectMember member);
    public void deleteMember(UUID id);
    public ProjectMember updateMember(ProjectMember member);
    public Optional<ProjectMember> findMemberById(UUID id);
    public List<ProjectMember> getAllWorkspaceMember();
}
