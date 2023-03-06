package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    public Project createProject(Project project);

}
