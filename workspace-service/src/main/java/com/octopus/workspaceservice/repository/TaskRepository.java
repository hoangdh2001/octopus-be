package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.model.Project;
import com.octopus.workspaceservice.model.Space;
import com.octopus.workspaceservice.model.Task;
import com.octopus.workspaceservice.model.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("SELECT t FROM Task t INNER JOIN TaskStatus ts ON t.id=ts.task.id WHERE ts.name like '%?1' ")
    public Task findByKeyword(String key);

    @Query("select t from Task t where t.status = true")
    public List<Task> findAllTask();

    @Query("SELECT t from Task t where day(t.createTime) = ?1 and month(t.createTime) = ?2 and year(t.createTime) = ?3")
    public List<Task> findByDay(int day, int month, int year);
}
