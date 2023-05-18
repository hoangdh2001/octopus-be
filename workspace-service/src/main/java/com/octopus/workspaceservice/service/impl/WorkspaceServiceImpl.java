package com.octopus.workspaceservice.service.impl;

import com.octopus.dtomodels.Payload;
import com.octopus.dtomodels.UserDTO;
import com.octopus.dtomodels.WorkspaceDTO;
import com.octopus.workspaceservice.dtos.request.WorkspaceRequest;
import com.octopus.workspaceservice.mapper.WorkspaceMapper;
import com.octopus.workspaceservice.models.Workspace;
import com.octopus.workspaceservice.models.WorkspaceMember;
import com.octopus.workspaceservice.repository.WorkspaceMemberRepository;
import com.octopus.workspaceservice.repository.WorkspaceRepository;
import com.octopus.workspaceservice.service.WorkspaceService;
import com.octopus.workspaceservice.specification.WorkspaceSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceMapper workspaceMapper;
    private final WebClient.Builder webClientBuilder;

    @Override
    @Transactional
    public WorkspaceDTO createNewWorkspace(WorkspaceRequest workspaceRequest) {
        var newWorkspace = Workspace.builder()
                .name(workspaceRequest.getName())
                .status(true)
                .build();
//        var workspaceMembers = workspaceRequest.getMembers().stream().map(s -> WorkspaceMember.builder()
//                .memberID(UUID.fromString(s))
//                .workspace(newWorkspace)
//                .build()).collect(Collectors.toList());
//        newWorkspace.setWorkspaceMembers(workspaceMembers);
        var workspace = this.workspaceRepository.save(newWorkspace);
        return this.workspaceMapper.mapToWorkspaceDTO(workspace);
    }

    @Override
    @Transactional
    public List<UserDTO> addMembers(String workspaceID, List<String> members, String token) {
        var workspace = Workspace.builder()
                .id(UUID.fromString(workspaceID))
                .build();
        var workspaceMembers = members.stream().map(s -> WorkspaceMember.builder()
                .memberID(UUID.fromString(s))
                .workspace(workspace)
                .build()).collect(Collectors.toList());
        this.workspaceMemberRepository.saveAll(workspaceMembers);
        return workspaceMembers.stream().map(workspaceMember -> findUserByID(workspaceMember.getMemberID().toString(), token)).collect(Collectors.toList());
    }

    private UserDTO findUserByID(String id, String token) {
        log.info(token);
        return webClientBuilder
                .build()
                .get().uri("http://auth-service/api/users", uriBuilder -> uriBuilder.path("/{id}").build(id))
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(UserDTO.class)
                .blockFirst();
    }

    @Override
    public List<WorkspaceDTO> searchWorkspace(Payload payload) {
        var spec = new WorkspaceSpecification(payload);
        var pageable = WorkspaceSpecification.getPageable(payload.getPage(), payload.getSize());
        var workspaces = this.workspaceRepository.findAll(spec, pageable);
        return workspaceMapper.mapListWorkspaceToWorkspaceDTO(workspaces.getContent());
    }

//    @Override
//    public Workspace createWorkspace(Workspace workSpace) {
//        return this.workspaceRepository.save(workSpace);
//    }
//
//    @Override
//    public void deleteWorkspace(UUID id) {
//        this.workspaceRepository.findById(id).get().setStatus(false);
//        //this.workspaceRepository.deleteById(id);
//    }
//
//    @Override
//    public Workspace updateWorkspace(Workspace workSpace) {
//        return this.workspaceRepository.save(workSpace);
//    }
//
//    @Override
//    public Workspace searchWorkspace(String key) {
//        return this.workspaceRepository.fillWorkspaceByKeywords(key);
//    }
//
//    @Override
//    public Optional<Workspace> findById(UUID id) {
//        return this.workspaceRepository.findById(id);
//    }
//
//    @Override
//    public List<Workspace> getAllWorkspace() {
//        return this.workspaceRepository.findAllWorkspace();
//    }
//
//    @Override
//    public List<WorkspaceDTO> search(Payload payload) {
//        var spec = new WorkspaceSpecification(payload);
//        var pageable = WorkspaceSpecification.getPageable(payload.getPage(), payload.getSize());
//        var workspaces = workspaceRepository.findAll(spec, pageable).getContent();
//        return workspaceMapper.mapListWorkspaceToWorkspaceDTO(workspaces);
//    }


}
