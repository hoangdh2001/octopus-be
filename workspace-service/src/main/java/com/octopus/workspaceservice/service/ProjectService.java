package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.models.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectService {
    public static final int PROJECT_PER_PAGE = 4;
    public Project createProject(Project project);
    public void deleteProject(UUID id);
    public Project updateProject(Project project);
    public Optional<Project> findById(UUID id);
    public Project searchProject(String key);
    public List<Project> getAllProject();
}
