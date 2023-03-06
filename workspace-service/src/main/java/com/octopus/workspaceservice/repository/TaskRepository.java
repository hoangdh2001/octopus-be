package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
