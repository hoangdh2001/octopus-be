package com.octopus.workspaceservice.service.impl;

import com.octopus.workspaceservice.models.ProjectMember;
import com.octopus.workspaceservice.models.WorkspaceMember;
import com.octopus.workspaceservice.repository.ProjectMemberRepository;
import com.octopus.workspaceservice.repository.WorkspaceMemberRepository;
import com.octopus.workspaceservice.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceIpml implements ProjectMemberService {
    private  final ProjectMemberRepository projectMemberRepository;

//    @Override
//    public ProjectMember addMember(ProjectMember member) {
//        return this.projectMemberRepository.save(member);
//    }
//
//    @Override
//    public void deleteMember(UUID id) {
//        this.projectMemberRepository.deleteById(id);
//    }
//
//    @Override
//    public ProjectMember updateMember(ProjectMember member) {
//        return this.projectMemberRepository.save(member);
//    }
//
//    @Override
//    public Optional<ProjectMember> findMemberById(UUID id) {
//        return this.projectMemberRepository.findById(id);
//    }
//
//    @Override
//    public List<ProjectMember> getAllWorkspaceMember() {
//        return this.projectMemberRepository.findAll();
//    }
}
