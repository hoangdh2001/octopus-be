package com.octopus.workspaceservice.service.impl;

import com.octopus.workspaceservice.model.RoleProject;
import com.octopus.workspaceservice.repository.RoleProjectRepository;
import com.octopus.workspaceservice.repository.RoleWorkspaceRepository;
import com.octopus.workspaceservice.service.RoleProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleProjectServiceImpl implements RoleProjectService {

    private final RoleProjectRepository roleProjectRepository;

    @Override
    public RoleProject addRoleProject(RoleProject roleProject) {
        return this.roleProjectRepository.save(roleProject);
    }

    @Override
    public RoleProject updateRoleProject(RoleProject roleProject) {
        return this.roleProjectRepository.save(roleProject);
    }

    @Override
    public void deleteRoleProject(int id) {
        this.roleProjectRepository.deleteById(id);
    }

    @Override
    public Optional<RoleProject> findRoleProjectById(int id) {
        return this.roleProjectRepository.findById(id);
    }

    @Override
    public List<RoleProject> getAllRoleProject() {
        return this.roleProjectRepository.findAll();
    }
}
