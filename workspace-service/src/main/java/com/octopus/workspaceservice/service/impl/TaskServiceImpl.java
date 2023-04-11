package com.octopus.workspaceservice.service.impl;

import com.octopus.workspaceservice.model.Task;
import com.octopus.workspaceservice.repository.TaskRepository;
import com.octopus.workspaceservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public Task createTask(Task task) {
        return this.taskRepository.save(task);
    }

    @Override
    public void deleteTask(int id) {
        this.taskRepository.deleteById(id);
    }

    @Override
    public Task updateTask(Task task) {
        return this.taskRepository.save(task);
    }

    @Override
    public Optional<Task> findById(int id) {
        return this.taskRepository.findById(id);
    }

    @Override
    public Task searchTask(String key) {
        return this.taskRepository.findByKeyword(key);
    }
}
