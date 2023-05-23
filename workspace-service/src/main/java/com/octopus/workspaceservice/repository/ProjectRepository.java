package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.models.Project;
import com.octopus.workspaceservice.models.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("SELECT p FROM Project p WHERE p.name like '%?1' ")
    public Project findByKeyword(String key);

    @Query("select p from Project p where p.status = true")
    public List<Project> findAllProject();

    @Query("select p from Project p where p.spaces in (?1)")
    public Project findProjectBySpace(Space space);
}
