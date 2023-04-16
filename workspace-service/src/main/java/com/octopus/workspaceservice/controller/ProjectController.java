package com.octopus.workspaceservice.controller;

import com.octopus.workspaceservice.dto.request.*;
import com.octopus.workspaceservice.model.*;
import com.octopus.workspaceservice.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectController {

    @Autowired
    private RoleProjectService roleProjectService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectMemberService projectMemberService;
    @Autowired
    private WorkspaceService workspaceService;

    @Operation(summary = "Create Project", description = "Create Project")
    @PostMapping("/createWorkspace")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Create Project"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Project> createProject(@Valid @RequestBody ProjectRequest projectRequest){
        Project project = new Project();
        project.setName(projectRequest.getName());
        project.setAvatar(projectRequest.getAvatar());
        project.setKey(projectRequest.getKey());
        project.setStatus(projectRequest.isStatus());
        project.setCreateTime(projectRequest.getCreateTime());
        project.setUpdateTime(projectRequest.getUpdateTime());
        project.setDeleteTime(projectRequest.getDeleteTime());
        project.setWorkSpace(workspaceService.findById(projectRequest.getWorkspaceId()).get());

        return ResponseEntity.ok().body(projectService.createProject(project));
    }

    @Operation(summary = "Update project", description = "Update project")
    @PutMapping("/{project_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Update project"),
            @ApiResponse(responseCode = "402", description = "Unauthorized")
    })
    public ResponseEntity<Project> updateProject(@RequestBody ProjectRequest projectRequest, @PathVariable("project_id") Integer id){
        Optional<Project> project = projectService.findById(id);
        project.get().setName(projectRequest.getName());
        project.get().setAvatar(projectRequest.getAvatar());
        project.get().setStatus(projectRequest.isStatus());
        project.get().setCreateTime(projectRequest.getCreateTime());
        project.get().setUpdateTime(projectRequest.getUpdateTime());
        project.get().setDeleteTime(projectRequest.getDeleteTime());

        return ResponseEntity.ok().body(projectService.updateProject(project.get()));
    }

    @Operation(summary = "Delete project", description = "Delete project")
    @DeleteMapping("/{project_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Delete project"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    public ResponseEntity<?> deleteWorkspace(@PathVariable("project_id") Integer id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.ok().body("Project has been deleted successfully.");
    }

    @Operation(summary = "Find project by keywords", description = "Find project by keywords")
    @GetMapping("/{key}")
    @ApiResponses({
            @ApiResponse(responseCode = "203", description = "Find project by keywords"),
            @ApiResponse(responseCode = "405", description = "Not find project")
    })
    public ResponseEntity<Project> findByKeyWorks(@PathVariable("key") String key){
        return ResponseEntity.ok().body(projectService.searchProject(key));
    }

    @Operation(summary = "Get all data project", description = "Get all data project")
    @GetMapping("/")
    @ApiResponses({
            @ApiResponse(responseCode = "209", description = "Get all data project"),
            @ApiResponse(responseCode = "411", description = "Not find member")
    })
    public ResponseEntity<List<Project>> getAllProject(){
        return ResponseEntity.ok().body(projectService.getAllProject());
    }

    @Operation(summary = "Add project member", description = "Add project member")
    @PostMapping("/member/add")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Add project member"),
            @ApiResponse(responseCode = "406", description = "Unauthorized")
    })
    public ResponseEntity<WorkSpaceMember> addProjecteMember(@Valid @RequestBody ProjectRequest projectRequest){
        WorkSpaceMember member = new WorkSpaceMember();
        member.setDescription(projectRequest.getName());
        member.setUserId(projectRequest.getMemberId());

        return ResponseEntity.ok().body(projectMemberService.addMember(member));
    }

    @Operation(summary = "Update project member", description = "Update project member")
    @PutMapping("/member/{workspacemember_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "205", description = "Update project member"),
            @ApiResponse(responseCode = "407", description = "Unauthorized")
    })
    public ResponseEntity<WorkSpaceMember> updateWorkspaceMember(@RequestBody WorkspaceMemberRequest workspaceMemberRequest, @PathVariable("workspacemember_id") Integer id){
        Optional<WorkSpaceMember> space = projectMemberService.findMemberById(id);
        space.get().setDescription(workspaceMemberRequest.getDescription());
        space.get().setUserId(workspaceMemberRequest.getUserId());

        return ResponseEntity.ok().body(projectMemberService.updateMember(space.get()));
    }

    @Operation(summary = "Delete project member", description = "Delete project member")
    @DeleteMapping("member/{workspacemember_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "206", description = "Delete project"),
            @ApiResponse(responseCode = "408", description = "Unauthorized")
    })
    public ResponseEntity<?> deleteWorkspaceMember(@PathVariable("workspacemember_id") Integer id) {
        projectMemberService.deleteMember(id);
        return ResponseEntity.ok().body("Project member has been deleted successfully.");
    }

    @Operation(summary = "Find project member by id", description = "Find project member by id")
    @GetMapping("/member/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "207", description = "Find project member by id"),
            @ApiResponse(responseCode = "409", description = "Not find project")
    })
    public ResponseEntity<Optional<WorkSpaceMember>> findMemberById(@PathVariable("id") int id){
        return ResponseEntity.ok().body(projectMemberService.findMemberById(id));
    }

    @Operation(summary = "Get all data project member", description = "Get all data project member")
    @GetMapping("/member")
    @ApiResponses({
            @ApiResponse(responseCode = "208", description = "Get all data project member"),
            @ApiResponse(responseCode = "410", description = "Not find project")
    })
    public ResponseEntity<List<WorkSpaceMember>> getAllWorkspaceMember(){
        return ResponseEntity.ok().body(projectMemberService.getAllWorkspaceMember());
    }

    @Operation(summary = "Add Role project", description = "Add Role project")
    @PostMapping("/role/add")
    @ApiResponses({
            @ApiResponse(responseCode = "210", description = "Add Role Workspace"),
            @ApiResponse(responseCode = "412", description = "Unauthorized")
    })
    public ResponseEntity<RoleProject> addRole(@Valid @RequestBody RoleWorkspaceRequest roleWorkspaceRequest){
        RoleProject role = new RoleProject();
        role.setName(roleWorkspaceRequest.getName());
        role.setDescription(roleWorkspaceRequest.getDescription());
        role.setUserId(roleWorkspaceRequest.getUserId());

        return ResponseEntity.ok().body(roleProjectService.addRoleProject(role));
    }

    @Operation(summary = "Update Role Project", description = "Update Role Project")
    @PutMapping("/role/{role_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "211", description = "Update Role Project"),
            @ApiResponse(responseCode = "413", description = "Unauthorized")
    })
    public ResponseEntity<RoleProject> updateRole(@RequestBody RoleProjectRequest roleProjectRequest, @PathVariable("role_id") Integer id){
        Optional<RoleProject> role = roleProjectService.findRoleProjectById(id);
        role.get().setName(roleProjectRequest.getName());
        role.get().setDescription(roleProjectRequest.getDescription());
        role.get().setUserId(roleProjectRequest.getUserId());

        return ResponseEntity.ok().body(roleProjectService.updateRoleProject(role.get()));
    }

    @Operation(summary = "Delete Role Project", description = "Delete Role Project")
    @DeleteMapping("role/{role_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "212", description = "Delete Role Workspace"),
            @ApiResponse(responseCode = "414", description = "Unauthorized")
    })
    public ResponseEntity<?> deleteRole(@PathVariable("role_id") Integer id) {
        roleProjectService.deleteRoleProject(id);
        return ResponseEntity.ok().body("Role Project has been deleted successfully.");
    }

    @Operation(summary = "Find role Project by id", description = "Find role Project by id")
    @GetMapping("/role/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "213", description = "Find role Project by id"),
            @ApiResponse(responseCode = "415", description = "Not find Project")
    })
    public ResponseEntity<Optional<RoleProject>> findRoleById(@PathVariable("id") int id){
        return ResponseEntity.ok().body(roleProjectService.findRoleProjectById(id));
    }

    @Operation(summary = "Get all data role Project", description = "Get all data role Project")
    @GetMapping("/role")
    @ApiResponses({
            @ApiResponse(responseCode = "214", description = "Get all data role Project"),
            @ApiResponse(responseCode = "416", description = "Not find Project")
    })
    public ResponseEntity<List<RoleProject>> getAllRole(){
        return ResponseEntity.ok().body(roleProjectService.getAllRoleProject());
    }

}
