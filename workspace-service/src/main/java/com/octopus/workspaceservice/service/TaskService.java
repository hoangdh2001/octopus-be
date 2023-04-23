package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.models.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskService {
    public static final int TASK_PER_PAGE = 4;
    public Task createTask(Task task);
    public void deleteTask(UUID id);
    public Task updateTask(Task task);
    public Optional<Task> findById(UUID id);
    public Task searchTask(String key);
    public List<Task> findAllTask();
}
