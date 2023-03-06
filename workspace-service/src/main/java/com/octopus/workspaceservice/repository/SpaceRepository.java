package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.model.Space;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Integer> {
}
