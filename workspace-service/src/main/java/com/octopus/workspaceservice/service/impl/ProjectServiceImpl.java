package com.octopus.workspaceservice.service.impl;

import com.octopus.workspaceservice.model.Project;
import com.octopus.workspaceservice.repository.ProjectRepository;
import com.octopus.workspaceservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;

    @Override
    public Project createProject(Project project) {
        return this.projectRepository.save(project);
    }

    @Override
    public void deleteProject(int id) {
        this.projectRepository.findById(id).get().setStatus(false);
        //this.projectRepository.deleteById(id);
    }

    @Override
    public Project updateProject(Project project) {
        return this.projectRepository.save(project);
    }

    @Override
    public Optional<Project> findById(int id) {
        return this.projectRepository.findById(id);
    }

    @Override
    public Project searchProject(String key) {
        return this.projectRepository.findByKeyword(key);
    }

    @Override
    public List<Project> getAllProject() {
        return this.projectRepository.findAllProject();
    }
}
