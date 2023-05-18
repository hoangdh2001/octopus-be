package com.octopus.workspaceservice.controllers;

import com.octopus.workspaceservice.dtos.request.TaskRequest;
import com.octopus.workspaceservice.models.Task;
import com.octopus.workspaceservice.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

//    @Operation(summary = "Create task", description = "Create task")
//    @PostMapping("/createTask")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Create task"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized")
//    })
//    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest){
//        Task task = new Task();
//        task.setCreateTime(taskRequest.getCreateTime());
//        task.setCreatedDate(taskRequest.getCreatedDate());
//        task.setUpdatedDate(taskRequest.getUpdatedDate());
//        task.setDescription(taskRequest.getDescription());
//        task.setStatus(true);
//
//        return ResponseEntity.ok().body(taskService.createTask(task));
//    }
//
//    @Operation(summary = "Update Task", description = "Update task")
//    @PutMapping("/{task_id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "Update task"),
//            @ApiResponse(responseCode = "402", description = "Unauthorized")
//    })
//    public ResponseEntity<Task> updateTask(@RequestBody TaskRequest taskRequest, @PathVariable("task_id") UUID id){
//        Optional<Task> task = taskService.findById(id);
//        task.get().setCreateTime(taskRequest.getCreateTime());
//        task.get().setCreatedDate(taskRequest.getCreatedDate());
//        task.get().setUpdatedDate(taskRequest.getUpdatedDate());
//        task.get().setDescription(taskRequest.getDescription());
//        task.get().setStatus(true);
//
//        return ResponseEntity.ok().body(taskService.updateTask(task.get()));
//    }
//
//    @Operation(summary = "Delete task", description = "Delete task")
//    @DeleteMapping("/{task_id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "202", description = "Delete task"),
//            @ApiResponse(responseCode = "403", description = "Unauthorized")
//    })
//    public ResponseEntity<?> deleteTask(@PathVariable("task_id") UUID id) {
//        taskService.deleteTask(id);
//        return ResponseEntity.ok().body("task has been deleted successfully.");
//    }
//
//    @Operation(summary = "Find workspace by keywords", description = "Find workspace by keywords")
//    @GetMapping("/{key}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "203", description = "Find workspace by keywords"),
//            @ApiResponse(responseCode = "405", description = "Not find workspace")
//    })
//    public ResponseEntity<Task> findByKeyWorks(@PathVariable("key") String key){
//        return ResponseEntity.ok().body(taskService.searchTask(key));
//    }
}
