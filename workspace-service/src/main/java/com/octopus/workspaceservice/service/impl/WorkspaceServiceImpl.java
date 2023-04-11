package com.octopus.workspaceservice.service.impl;

import com.octopus.workspaceservice.model.Project;
import com.octopus.workspaceservice.model.WorkSpace;
import com.octopus.workspaceservice.model.WorkSpaceMember;
import com.octopus.workspaceservice.repository.WorkspaceMemberRepository;
import com.octopus.workspaceservice.repository.WorkspaceRepository;
import com.octopus.workspaceservice.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;

    @Override
    public WorkSpace createWorkspace(WorkSpace workSpace) {
        return this.workspaceRepository.save(workSpace);
    }

    @Override
    public void deleteWorkspace(int id) {
        this.workspaceRepository.deleteById(id);
    }

    @Override
    public WorkSpace updateWorkspace(WorkSpace workSpace) {
        return this.workspaceRepository.save(workSpace);
    }

    @Override
    public WorkSpace searchWorkspace(String key) {
        return this.workspaceRepository.fillWorkspaceByKeywords(key);
    }

    @Override
    public Optional<WorkSpace> findById(int id) {
        return this.workspaceRepository.findById(id);
    }

}
