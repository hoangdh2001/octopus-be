package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.model.Project;
import com.octopus.workspaceservice.model.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpaceRepository extends JpaRepository<Space, Integer> {
    @Query("SELECT s FROM Space s INNER JOIN Columns c ON s.id=c.space.id WHERE s.name like '%?1' ")
    public Space findByKeyword(String key);
}
