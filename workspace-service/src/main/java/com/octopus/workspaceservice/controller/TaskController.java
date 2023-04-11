package com.octopus.workspaceservice.controller;

import com.octopus.workspaceservice.dto.request.SpaceRequest;
import com.octopus.workspaceservice.dto.request.TaskRequest;
import com.octopus.workspaceservice.model.Space;
import com.octopus.workspaceservice.model.Task;
import com.octopus.workspaceservice.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Operation(summary = "Create task", description = "Create task")
    @PostMapping("/createTask")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Create task"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest){
        Task task = new Task();
        task.setCreateTime(taskRequest.getCreateTime());
        task.setCreateBy(taskRequest.getCreateBy());
        task.setUpdateTime(taskRequest.getUpdateTime());
        task.setDescription(taskRequest.getDescription());

        return ResponseEntity.ok().body(taskService.createTask(task));
    }

    @Operation(summary = "Update Task", description = "Update task")
    @PutMapping("/{task_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Update task"),
            @ApiResponse(responseCode = "402", description = "Unauthorized")
    })
    public ResponseEntity<Task> updateTask(@RequestBody TaskRequest taskRequest, @PathVariable("task_id") Integer id){
        Optional<Task> task = taskService.findById(id);
        task.get().setCreateTime(taskRequest.getCreateTime());
        task.get().setCreateBy(taskRequest.getCreateBy());
        task.get().setUpdateTime(taskRequest.getUpdateTime());
        task.get().setDescription(taskRequest.getDescription());

        return ResponseEntity.ok().body(taskService.updateTask(task.get()));
    }

    @Operation(summary = "Delete task", description = "Delete task")
    @DeleteMapping("/{task_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Delete task"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    public ResponseEntity<?> deleteTask(@PathVariable("task_id") Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().body("task has been deleted successfully.");
    }

    @Operation(summary = "Find workspace by keywords", description = "Find workspace by keywords")
    @GetMapping("/{key}")
    @ApiResponses({
            @ApiResponse(responseCode = "203", description = "Find workspace by keywords"),
            @ApiResponse(responseCode = "405", description = "Not find workspace")
    })
    public ResponseEntity<Task> findByKeyWorks(@PathVariable("key") String key){
        return ResponseEntity.ok().body(taskService.searchTask(key));
    }
}
