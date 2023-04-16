package com.octopus.workspaceservice.controller;

import com.octopus.authutils.SecurityUtils;
import com.octopus.workspaceservice.dto.request.RoleWorkspaceRequest;
import com.octopus.workspaceservice.dto.request.WorkspaceMemberRequest;
import com.octopus.workspaceservice.dto.request.WorkspaceRequest;
import com.octopus.workspaceservice.model.RoleWorkSpace;
import com.octopus.workspaceservice.model.WorkSpace;
import com.octopus.workspaceservice.model.WorkSpaceMember;
import com.octopus.workspaceservice.service.RoleWorkspaceService;
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
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/workspaces")
@CrossOrigin
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private WorkspaceMemberService workspaceMemberService;
    @Autowired
    private RoleWorkspaceService roleWorkspaceService;

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

    @Operation(summary = "Get all data workspace", description = "Get all data workspace")
    @GetMapping("/")
    @ApiResponses({
            @ApiResponse(responseCode = "209", description = "Get all data workspace"),
            @ApiResponse(responseCode = "411", description = "Not find member")
    })
    public ResponseEntity<List<WorkSpace>> getAllWorkspace(){
        return ResponseEntity.ok().body(workspaceService.getAllWorkspace());
    }

    @Operation(summary = "Add Workspace member", description = "Add Workspace member")
    @PostMapping("/member/add")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Add Workspace member"),
            @ApiResponse(responseCode = "406", description = "Unauthorized")
    })
    public ResponseEntity<WorkSpaceMember> addWorkspaceMember(@Valid @RequestBody WorkspaceMemberRequest workspaceMemberRequest){
        WorkSpaceMember space = new WorkSpaceMember();
        SecurityUtils securityUtils = new SecurityUtils();
        space.setDescription(workspaceMemberRequest.getDescription());
        space.setUserId(Integer.getInteger(securityUtils.getCurrentUser()));

        return ResponseEntity.ok().body(workspaceMemberService.addMember(space));
    }

    @Operation(summary = "Update Workspace member", description = "Update Workspace member")
    @PutMapping("/member/{workspacemember_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "205", description = "Update Workspace member"),
            @ApiResponse(responseCode = "407", description = "Unauthorized")
    })
    public ResponseEntity<WorkSpaceMember> updateWorkspaceMember(@RequestBody WorkspaceMemberRequest workspaceMemberRequest, @PathVariable("workspacemember_id") Integer id){
        Optional<WorkSpaceMember> space = workspaceMemberService.findMemberById(id);
        SecurityUtils securityUtils = new SecurityUtils();
        space.get().setDescription(workspaceMemberRequest.getDescription());
        space.get().setUserId(Integer.getInteger(securityUtils.getCurrentUser()));

        return ResponseEntity.ok().body(workspaceMemberService.updateMember(space.get()));
    }

    @Operation(summary = "Delete Workspace member", description = "Delete Workspace member")
    @DeleteMapping("member/{workspacemember_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "206", description = "Delete Workspace"),
            @ApiResponse(responseCode = "408", description = "Unauthorized")
    })
    public ResponseEntity<?> deleteWorkspaceMember(@PathVariable("workspacemember_id") Integer id) {
        workspaceMemberService.deleteMember(id);
        return ResponseEntity.ok().body("Workspace member has been deleted successfully.");
    }

    @Operation(summary = "Find workspace member by id", description = "Find workspace member by id")
    @GetMapping("/member/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "207", description = "Find workspace member by id"),
            @ApiResponse(responseCode = "409", description = "Not find workspace")
    })
    public ResponseEntity<Optional<WorkSpaceMember>> findMemberById(@PathVariable("id") int id){
        return ResponseEntity.ok().body(workspaceMemberService.findMemberById(id));
    }

    @Operation(summary = "Get all data workspace member", description = "Get all data workspace member")
    @GetMapping("/member")
    @ApiResponses({
            @ApiResponse(responseCode = "208", description = "Get all data workspace member"),
            @ApiResponse(responseCode = "410", description = "Not find member")
    })
    public ResponseEntity<List<WorkSpaceMember>> getAllWorkspaceMember(){
        return ResponseEntity.ok().body(workspaceMemberService.getAllWorkspaceMember());
    }

    @Operation(summary = "Add Role Workspace", description = "Add Role Workspace")
    @PostMapping("/role/add")
    @ApiResponses({
            @ApiResponse(responseCode = "210", description = "Add Role Workspace"),
            @ApiResponse(responseCode = "412", description = "Unauthorized")
    })
    public ResponseEntity<RoleWorkSpace> addRole(@Valid @RequestBody RoleWorkspaceRequest roleWorkspaceRequest){
        RoleWorkSpace role = new RoleWorkSpace();
        SecurityUtils securityUtils = new SecurityUtils();
        role.setName(roleWorkspaceRequest.getName());
        role.setDescription(roleWorkspaceRequest.getDescription());
        role.setUserId(Integer.getInteger(securityUtils.getCurrentUser()));

        return ResponseEntity.ok().body(roleWorkspaceService.addRoleWorkspace(role));
    }

    @Operation(summary = "Update Role Workspace", description = "Update Role Workspace")
    @PutMapping("/role/{role_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "211", description = "Update Role Workspace"),
            @ApiResponse(responseCode = "413", description = "Unauthorized")
    })
    public ResponseEntity<RoleWorkSpace> updateRole(@RequestBody RoleWorkspaceRequest roleWorkspaceRequest, @PathVariable("role_id") Integer id){
        Optional<RoleWorkSpace> role = roleWorkspaceService.findRoleWorkspaceById(id);
        SecurityUtils securityUtils = new SecurityUtils();
        role.get().setName(roleWorkspaceRequest.getName());
        role.get().setDescription(roleWorkspaceRequest.getDescription());
        role.get().setUserId(Integer.getInteger(securityUtils.getCurrentUser()));

        return ResponseEntity.ok().body(roleWorkspaceService.updateRoleWorkspace(role.get()));
    }

    @Operation(summary = "Delete Role Workspace", description = "Delete Role Workspace")
    @DeleteMapping("role/{role_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "212", description = "Delete Role Workspace"),
            @ApiResponse(responseCode = "414", description = "Unauthorized")
    })
    public ResponseEntity<?> deleteRole(@PathVariable("role_id") Integer id) {
        roleWorkspaceService.deleteRoleWorkspace(id);
        return ResponseEntity.ok().body("Role Workspace has been deleted successfully.");
    }

    @Operation(summary = "Find role workspace by id", description = "Find role workspace by id")
    @GetMapping("/role/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "213", description = "Find role workspace by id"),
            @ApiResponse(responseCode = "415", description = "Not find workspace")
    })
    public ResponseEntity<Optional<RoleWorkSpace>> findRoleById(@PathVariable("id") int id){
        return ResponseEntity.ok().body(roleWorkspaceService.findRoleWorkspaceById(id));
    }

    @Operation(summary = "Get all data role workspace", description = "Get all data role workspace")
    @GetMapping("/role")
    @ApiResponses({
            @ApiResponse(responseCode = "214", description = "Get all data role workspace"),
            @ApiResponse(responseCode = "416", description = "Not find member")
    })
    public ResponseEntity<List<RoleWorkSpace>> getAllRole(){
        return ResponseEntity.ok().body(roleWorkspaceService.getAllRoleWorkspace());
    }

}
