package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.model.Project;
import com.octopus.workspaceservice.model.Task;

import java.util.Optional;

public interface TaskService {
    public static final int TASK_PER_PAGE = 4;
    public Task createTask(Task task);
    public void deleteTask(int id);
    public Task updateTask(Task task);
    public Optional<Task> findById(int id);
    public Task searchTask(String key);
}
