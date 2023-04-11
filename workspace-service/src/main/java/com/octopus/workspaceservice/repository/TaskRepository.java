package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.model.Project;
import com.octopus.workspaceservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("SELECT t FROM Task t INNER JOIN TaskStatus ts ON t.id=ts.task.id WHERE ts.name like '%?1' ")
    public Task findByKeyword(String key);
}
