package com.octopus.workspaceservice.service.impl;

import com.octopus.workspaceservice.models.Workspace;
import com.octopus.workspaceservice.repository.WorkspaceMemberRepository;
import com.octopus.workspaceservice.repository.WorkspaceRepository;
import com.octopus.workspaceservice.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;

    @Override
    public Workspace createWorkspace(Workspace workSpace) {
        return this.workspaceRepository.save(workSpace);
    }

    @Override
    public void deleteWorkspace(UUID id) {
        this.workspaceRepository.findById(id).get().setStatus(false);
        //this.workspaceRepository.deleteById(id);
    }

    @Override
    public Workspace updateWorkspace(Workspace workSpace) {
        return this.workspaceRepository.save(workSpace);
    }

    @Override
    public Workspace searchWorkspace(String key) {
        return this.workspaceRepository.fillWorkspaceByKeywords(key);
    }

    @Override
    public Optional<Workspace> findById(UUID id) {
        return this.workspaceRepository.findById(id);
    }

    @Override
    public List<Workspace> getAllWorkspace() {
        return this.workspaceRepository.findAllWorkspace();
    }


}
