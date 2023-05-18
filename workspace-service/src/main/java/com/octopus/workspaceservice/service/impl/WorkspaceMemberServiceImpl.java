package com.octopus.workspaceservice.service.impl;

import com.octopus.dtomodels.UserDTO;
import com.octopus.workspaceservice.models.WorkspaceMember;
import com.octopus.workspaceservice.repository.WorkspaceMemberRepository;
import com.octopus.workspaceservice.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {
    private  final WorkspaceMemberRepository workspaceMemberRepository;

//    private final WebClient webClient;
//
//    @Override
//    public WorkspaceMember addMember(WorkspaceMember member) {
//        return this.workspaceMemberRepository.save(member);
//    }
//
//    @Override
//    public List<UserDTO> addMembers(List<WorkspaceMember> workspaceMembers) {
//        var newWorkspaceMembers = this.workspaceMemberRepository.saveAll(workspaceMembers);
//        List<UserDTO> members = new ArrayList<>();
//        for (WorkspaceMember workspaceMember: newWorkspaceMembers) {
//            var user = webClient.get().uri(uriBuilder ->
//                    uriBuilder.path("http://auth-service/api/users/{id}").build(workspaceMember.getMemberID()
//                    )
//            )
//                    .retrieve()
//                    .bodyToFlux(UserDTO.class)
//                    .blockFirst();
//            members.add(user);
//        }
//        return members;
//    }

    //
//    @Override
//    public void deleteMember(UUID id) {
//        this.workspaceMemberRepository.deleteById(id);
//    }
//
//    @Override
//    public WorkspaceMember updateMember(WorkspaceMember member) {
//        return this.workspaceMemberRepository.save(member);
//    }
//
//    @Override
//    public Optional<WorkspaceMember> findMemberById(UUID id) {
//        return this.workspaceMemberRepository.findById(id);
//    }
//
//    @Override
//    public List<WorkspaceMember> getAllWorkspaceMember() {
//        return this.workspaceMemberRepository.findAll();
//    }
}
