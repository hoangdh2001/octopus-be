package com.octopus.workspaceservice.controllers;

import com.octopus.authutils.SecurityUtils;
import com.octopus.workspaceservice.dtos.request.ProjectMemberRequest;
import com.octopus.workspaceservice.dtos.request.ProjectRequest;
import com.octopus.workspaceservice.dtos.request.RoleProjectRequest;
import com.octopus.workspaceservice.models.Project;
import com.octopus.workspaceservice.models.ProjectMember;
import com.octopus.workspaceservice.models.RoleProject;
import com.octopus.workspaceservice.models.WorkspaceMember;
import com.octopus.workspaceservice.service.ProjectMemberService;
import com.octopus.workspaceservice.service.ProjectService;
import com.octopus.workspaceservice.service.RoleProjectService;
import com.octopus.workspaceservice.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
@RequiredArgsConstructor
public class ProjectController {

    private final RoleProjectService roleProjectService;
    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final WorkspaceService workspaceService;

//    @Operation(summary = "Create Project", description = "Create Project")
//    @PostMapping("/createProject")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Create Project"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized")
//    })
//    public ResponseEntity<Project> createProject(@Valid @RequestBody ProjectRequest projectRequest){
//        Project project = new Project();
//        project.setName(projectRequest.getName());
//        project.setAvatar(projectRequest.getAvatar());
//        project.setStatus(true);
//        project.setCreatedDate(projectRequest.getCreateDate());
//        project.setUpdatedDate(projectRequest.getUpdateDate());
//        project.setDeletedDate(projectRequest.getDeleteDate());
//        //project.setWorkSpace(workspaceService.findById(projectRequest.getWorkspaceId()).get());
//
//        return ResponseEntity.ok().body(projectService.createProject(project));
//    }
//
//    @Operation(summary = "Update project", description = "Update project")
//    @PutMapping("/{project_id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "Update project"),
//            @ApiResponse(responseCode = "402", description = "Unauthorized")
//    })
//    public ResponseEntity<Project> updateProject(@RequestBody ProjectRequest projectRequest, @PathVariable("project_id") UUID id){
//        Optional<Project> project = projectService.findById(id);
//        project.get().setName(projectRequest.getName());
//        project.get().setAvatar(projectRequest.getAvatar());
//        project.get().setStatus(true);
//        project.get().setCreatedDate(projectRequest.getCreateDate());
//        project.get().setUpdatedDate(projectRequest.getUpdateDate());
//        project.get().setDeletedDate(projectRequest.getDeleteDate());
//
//        return ResponseEntity.ok().body(projectService.updateProject(project.get()));
//    }
//
//    @Operation(summary = "Delete project", description = "Delete project")
//    @DeleteMapping("/{project_id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "202", description = "Delete project"),
//            @ApiResponse(responseCode = "403", description = "Unauthorized")
//    })
//    public ResponseEntity<?> deleteWorkspace(@PathVariable("project_id") UUID id) {
//        workspaceService.deleteWorkspace(id);
//        return ResponseEntity.ok().body("Project has been deleted successfully.");
//    }
//
//    @Operation(summary = "Find project by keywords", description = "Find project by keywords")
//    @GetMapping("/{key}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "203", description = "Find project by keywords"),
//            @ApiResponse(responseCode = "405", description = "Not find project")
//    })
//    public ResponseEntity<Project> findByKeyWorks(@PathVariable("key") String key){
//        return ResponseEntity.ok().body(projectService.searchProject(key));
//    }
//
//    @Operation(summary = "Get all data project", description = "Get all data project")
//    @GetMapping("/")
//    @ApiResponses({
//            @ApiResponse(responseCode = "209", description = "Get all data project"),
//            @ApiResponse(responseCode = "411", description = "Not find member")
//    })
//    public ResponseEntity<List<Project>> getAllProject(){
//        return ResponseEntity.ok().body(projectService.getAllProject());
//    }
//
//    @Operation(summary = "Add project member", description = "Add project member")
//    @PostMapping("/member/add")
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "Add project member"),
//            @ApiResponse(responseCode = "406", description = "Unauthorized")
//    })
//    public ResponseEntity<ProjectMember> addProjecteMember(@Valid @RequestBody ProjectMemberRequest projectMemberRequest){
//        ProjectMember member = new ProjectMember();
//        SecurityUtils securityUtils = new SecurityUtils();
//        member.setProject(projectMemberRequest.getProject());
//        member.setMemberID(UUID.fromString(securityUtils.getCurrentUser()));
//
//        return ResponseEntity.ok().body(projectMemberService.addMember(member));
//    }
//
//    @Operation(summary = "Update project member", description = "Update project member")
//    @PutMapping("/member/{workspacemember_id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "205", description = "Update project member"),
//            @ApiResponse(responseCode = "407", description = "Unauthorized")
//    })
//    public ResponseEntity<ProjectMember> updateProjectMember(@RequestBody ProjectMemberRequest projectMemberRequest, @PathVariable("workspacemember_id") UUID id){
//        Optional<ProjectMember> space = projectMemberService.findMemberById(id);
//        SecurityUtils securityUtils = new SecurityUtils();
//        space.get().setProject(projectMemberRequest.getProject());
//        space.get().setMemberID(UUID.fromString(securityUtils.getCurrentUser()));
//
//        return ResponseEntity.ok().body(projectMemberService.updateMember(space.get()));
//    }
//
//    @Operation(summary = "Delete project member", description = "Delete project member")
//    @DeleteMapping("member/{workspacemember_id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "206", description = "Delete project"),
//            @ApiResponse(responseCode = "408", description = "Unauthorized")
//    })
//    public ResponseEntity<?> deleteWorkspaceMember(@PathVariable("workspacemember_id") UUID id) {
//        projectMemberService.deleteMember(id);
//        return ResponseEntity.ok().body("Project member has been deleted successfully.");
//    }
//
//    @Operation(summary = "Find project member by id", description = "Find project member by id")
//    @GetMapping("/member/{id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "207", description = "Find project member by id"),
//            @ApiResponse(responseCode = "409", description = "Not find project")
//    })
//    public ResponseEntity<Optional<ProjectMember>> findMemberById(@PathVariable("id") UUID id){
//        return ResponseEntity.ok().body(projectMemberService.findMemberById(id));
//    }
//
//    @Operation(summary = "Get all data project member", description = "Get all data project member")
//    @GetMapping("/member")
//    @ApiResponses({
//            @ApiResponse(responseCode = "208", description = "Get all data project member"),
//            @ApiResponse(responseCode = "410", description = "Not find project")
//    })
//    public ResponseEntity<List<ProjectMember>> getAllWorkspaceMember(){
//        return ResponseEntity.ok().body(projectMemberService.getAllWorkspaceMember());
//    }
//
//    @Operation(summary = "Add Role project", description = "Add Role project")
//    @PostMapping("/role/add")
//    @ApiResponses({
//            @ApiResponse(responseCode = "210", description = "Add Role Workspace"),
//            @ApiResponse(responseCode = "412", description = "Unauthorized")
//    })
//    public ResponseEntity<RoleProject> addRole(@Valid @RequestBody RoleProjectRequest roleProjectRequest){
//        RoleProject role = new RoleProject();
//        SecurityUtils securityUtils = new SecurityUtils();
//        role.setName(roleProjectRequest.getName());
//        role.setDescription(roleProjectRequest.getDescription());
//        role.setUserId(UUID.fromString(securityUtils.getCurrentUser()));
//
//        return ResponseEntity.ok().body(roleProjectService.addRoleProject(role));
//    }
//
//    @Operation(summary = "Update Role Project", description = "Update Role Project")
//    @PutMapping("/role/{role_id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "211", description = "Update Role Project"),
//            @ApiResponse(responseCode = "413", description = "Unauthorized")
//    })
//    public ResponseEntity<RoleProject> updateRole(@RequestBody RoleProjectRequest roleProjectRequest, @PathVariable("role_id") UUID id){
//        Optional<RoleProject> role = roleProjectService.findRoleProjectById(id);
//        SecurityUtils securityUtils = new SecurityUtils();
//        role.get().setName(roleProjectRequest.getName());
//        role.get().setDescription(roleProjectRequest.getDescription());
//        role.get().setUserId(UUID.fromString(securityUtils.getCurrentUser()));
//
//        return ResponseEntity.ok().body(roleProjectService.updateRoleProject(role.get()));
//    }
//
//    @Operation(summary = "Delete Role Project", description = "Delete Role Project")
//    @DeleteMapping("role/{role_id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "212", description = "Delete Role Workspace"),
//            @ApiResponse(responseCode = "414", description = "Unauthorized")
//    })
//    public ResponseEntity<?> deleteRole(@PathVariable("role_id") UUID id) {
//        roleProjectService.deleteRoleProject(id);
//        return ResponseEntity.ok().body("Role Project has been deleted successfully.");
//    }
//
//    @Operation(summary = "Find role Project by id", description = "Find role Project by id")
//    @GetMapping("/role/{id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "213", description = "Find role Project by id"),
//            @ApiResponse(responseCode = "415", description = "Not find Project")
//    })
//    public ResponseEntity<Optional<RoleProject>> findRoleById(@PathVariable("id") UUID id){
//        return ResponseEntity.ok().body(roleProjectService.findRoleProjectById(id));
//    }
//
//    @Operation(summary = "Get all data role Project", description = "Get all data role Project")
//    @GetMapping("/role")
//    @ApiResponses({
//            @ApiResponse(responseCode = "214", description = "Get all data role Project"),
//            @ApiResponse(responseCode = "416", description = "Not find Project")
//    })
//    public ResponseEntity<List<RoleProject>> getAllRole(){
//        return ResponseEntity.ok().body(roleProjectService.getAllRoleProject());
//    }

}
