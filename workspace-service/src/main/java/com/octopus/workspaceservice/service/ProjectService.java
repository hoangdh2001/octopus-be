package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    public static final int PROJECT_PER_PAGE = 4;
    public Project createProject(Project project);
    public void deleteProject(int id);
    public Project updateProject(Project project);
    public Optional<Project> findById(int id);
    public Project searchProject(String key);
    public List<Project> getAllProject();
}
