package com.octopus.workspaceservice.service.impl;

import com.octopus.workspaceservice.model.WorkSpaceMember;
import com.octopus.workspaceservice.repository.WorkspaceMemberRepository;
import com.octopus.workspaceservice.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {
    private  final WorkspaceMemberRepository workspaceMemberRepository;

    @Override
    public WorkSpaceMember addMember(WorkSpaceMember member) {
        return this.workspaceMemberRepository.save(member);
    }

    @Override
    public void deleteMember(int id) {
        this.workspaceMemberRepository.deleteById(id);
    }

    @Override
    public WorkSpaceMember updateMember(WorkSpaceMember member) {
        return this.workspaceMemberRepository.save(member);
    }

    @Override
    public Optional<WorkSpaceMember> findMemberById(int id) {
        return this.workspaceMemberRepository.findById(id);
    }

    @Override
    public List<WorkSpaceMember> getAllWorkspaceMember() {
        return this.workspaceMemberRepository.findAll();
    }
}
