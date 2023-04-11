package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.model.Project;
import com.octopus.workspaceservice.model.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Query("SELECT p FROM Project p WHERE p.name like '%?1' ")
    public Project findByKeyword(String key);
}
