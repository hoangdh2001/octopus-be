package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
//    @Query("SELECT t FROM Task t INNER JOIN TaskStatus ts ON t.id=ts.task.id WHERE ts.name like '%?1' ")
//    public Task findByKeyword(String key);
//
//    @Query("select t from Task t where t.status = true")
//    public List<Task> findAllTask();
//
//    @Query("SELECT t from Task t where day(t.createTime) = ?1 and month(t.createTime) = ?2 and year(t.createTime) = ?3")
//    public List<Task> findByDay(int day, int month, int year);

    @Query("select t from Workspace w join w.projects p join p.spaces s join s.tasks t where w.id = ?1 and CURRENT_TIMESTAMP between t.startDate and t.dueDate")
    Set<Task> findTaskToday(UUID workspaceID);

    @Query("select t from Workspace w join w.projects p join p.spaces s join s.tasks t where w.id = ?1 and t.dueDate <= CURRENT_TIMESTAMP")
    Set<Task> findTaskExpirationDate(UUID workspaceID);

    @Query("select t from Workspace w join w.projects p join p.spaces s join s.tasks t where w.id = ?1 and t.startDate >= current_date")
    Set<Task> findTaskNotStartDay(UUID workspaceID);

    @Query("select t from Workspace w join w.projects p join p.spaces s join s.tasks t where w.id = ?1 and t.startDate <= CURRENT_DATE and t.dueDate >= CURRENT_DATE")
    Set<Task> findTaskDateInTerm(UUID workspaceID);
}
