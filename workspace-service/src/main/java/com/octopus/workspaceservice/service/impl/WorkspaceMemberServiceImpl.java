package com.octopus.workspaceservice.service.impl;

import com.octopus.workspaceservice.models.WorkspaceMember;
import com.octopus.workspaceservice.repository.WorkspaceMemberRepository;
import com.octopus.workspaceservice.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {
    private  final WorkspaceMemberRepository workspaceMemberRepository;

    @Override
    public WorkspaceMember addMember(WorkspaceMember member) {
        return this.workspaceMemberRepository.save(member);
    }

    @Override
    public void deleteMember(UUID id) {
        this.workspaceMemberRepository.deleteById(id);
    }

    @Override
    public WorkspaceMember updateMember(WorkspaceMember member) {
        return this.workspaceMemberRepository.save(member);
    }

    @Override
    public Optional<WorkspaceMember> findMemberById(UUID id) {
        return this.workspaceMemberRepository.findById(id);
    }

    @Override
    public List<WorkspaceMember> getAllWorkspaceMember() {
        return this.workspaceMemberRepository.findAll();
    }
}
