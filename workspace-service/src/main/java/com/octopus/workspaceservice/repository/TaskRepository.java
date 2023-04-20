package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
//    @Query("SELECT t FROM Task t INNER JOIN TaskStatus ts ON t.id=ts.task.id WHERE ts.name like '%?1' ")
//    public Task findByKeyword(String key);
//
//    @Query("select t from Task t where t.status = true")
//    public List<Task> findAllTask();
//
//    @Query("SELECT t from Task t where day(t.createTime) = ?1 and month(t.createTime) = ?2 and year(t.createTime) = ?3")
//    public List<Task> findByDay(int day, int month, int year);
}
