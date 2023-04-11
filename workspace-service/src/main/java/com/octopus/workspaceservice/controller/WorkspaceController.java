package com.octopus.workspaceservice.controller;

import com.octopus.workspaceservice.dto.request.WorkspaceRequest;
import com.octopus.workspaceservice.model.WorkSpace;
import com.octopus.workspaceservice.service.WorkspaceMemberService;
import com.octopus.workspaceservice.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/workspaces")
@CrossOrigin
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private WorkspaceMemberService workspaceMemberService;

    @Operation(summary = "Create Workspace", description = "Create Workspace")
    @PostMapping("/createWorkspace")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Create Workspace"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<WorkSpace> createWorkspace(@Valid @RequestBody WorkspaceRequest workspaceRequest){
        WorkSpace space = new WorkSpace();
        space.setName(workspaceRequest.getName());
        space.setAvatar(workspaceRequest.getAvatar());
        space.setStatus(workspaceRequest.isStatus());
        space.setCreateTime(workspaceRequest.getCreateTime());
        space.setUpdateTime(workspaceRequest.getUpdateTime());
        space.setDeleteTime(workspaceRequest.getDeleteTime());

        return ResponseEntity.ok().body(workspaceService.createWorkspace(space));
    }

    @Operation(summary = "Update Workspace", description = "Update Workspace")
    @PutMapping("/{workspace_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Update Workspace"),
            @ApiResponse(responseCode = "402", description = "Unauthorized")
    })
    public ResponseEntity<WorkSpace> updateWorkspace(@RequestBody WorkspaceRequest workspaceRequest, @PathVariable("workspace_id") Integer id){
        Optional<WorkSpace> space = workspaceService.findById(id);
        space.get().setName(workspaceRequest.getName());
        space.get().setAvatar(workspaceRequest.getAvatar());
        space.get().setStatus(workspaceRequest.isStatus());
        space.get().setCreateTime(workspaceRequest.getCreateTime());
        space.get().setUpdateTime(workspaceRequest.getUpdateTime());
        space.get().setDeleteTime(workspaceRequest.getDeleteTime());

        return ResponseEntity.ok().body(workspaceService.updateWorkspace(space.get()));
    }

    @Operation(summary = "Delete Workspace", description = "Delete Workspace")
    @DeleteMapping("/{workspace_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Delete Workspace"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    public ResponseEntity<?> deleteWorkspace(@PathVariable("workspace_id") Integer id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.ok().body("Workspace has been deleted successfully.");
    }

    @Operation(summary = "Find workspace by keywords", description = "Find workspace by keywords")
    @GetMapping("/{key}")
    @ApiResponses({
            @ApiResponse(responseCode = "203", description = "Find workspace by keywords"),
            @ApiResponse(responseCode = "405", description = "Not find workspace")
    })
    public ResponseEntity<WorkSpace> findByKeyWorks(@PathVariable("key") String key){
        return ResponseEntity.ok().body(workspaceService.searchWorkspace(key));
    }

}
