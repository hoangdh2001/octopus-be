package com.octopus.workspaceservice.service.impl;

import com.octopus.workspaceservice.models.RoleWorkSpace;
import com.octopus.workspaceservice.repository.RoleWorkspaceRepository;
import com.octopus.workspaceservice.service.RoleWorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleWorkspaceServiceImpl implements RoleWorkspaceService {

    private final RoleWorkspaceRepository roleWorkspaceRepository;

    @Override
    public RoleWorkSpace addRoleWorkspace(RoleWorkSpace roleWorkSpace) {
        return this.roleWorkspaceRepository.save(roleWorkSpace);
    }

    @Override
    public RoleWorkSpace updateRoleWorkspace(RoleWorkSpace roleWorkSpace) {
        return this.roleWorkspaceRepository.save(roleWorkSpace);
    }

    @Override
    public void deleteRoleWorkspace(UUID id) {
        this.roleWorkspaceRepository.deleteById(id);
    }

    @Override
    public Optional<RoleWorkSpace> findRoleWorkspaceById(UUID id) {
        return this.roleWorkspaceRepository.findById(id);
    }

    @Override
    public List<RoleWorkSpace> getAllRoleWorkspace() {
        return this.roleWorkspaceRepository.findAll();
    }
}
