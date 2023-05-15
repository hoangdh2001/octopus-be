package com.octopus.workspaceservice.controllers;

import com.octopus.authutils.SecurityUtils;
import com.octopus.dtomodels.Code;
import com.octopus.workspaceservice.dtos.request.RoleWorkspaceRequest;
import com.octopus.workspaceservice.dtos.request.WorkspaceMemberRequest;
import com.octopus.workspaceservice.dtos.request.WorkspaceRequest;
import com.octopus.workspaceservice.kafka.KafkaProducer;
import com.octopus.workspaceservice.models.RoleWorkSpace;
import com.octopus.workspaceservice.models.Workspace;
import com.octopus.workspaceservice.models.WorkspaceMember;
import com.octopus.workspaceservice.service.RoleWorkspaceService;
import com.octopus.workspaceservice.service.WorkspaceMemberService;
import com.octopus.workspaceservice.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/workspaces")
@CrossOrigin
@RequiredArgsConstructor
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private WorkspaceMemberService workspaceMemberService;
    @Autowired
    private RoleWorkspaceService roleWorkspaceService;

    private final KafkaProducer kafkaProducer;

    //private final VerificationCodeService verificationCodeService;

    @Operation(summary = "Create Workspace", description = "Create Workspace")
    @PostMapping("/createWorkspace")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Create Workspace"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Workspace> createWorkspace(@Valid @RequestBody WorkspaceRequest workspaceRequest){
        Workspace space = new Workspace();
        space.setName(workspaceRequest.getName());
        space.setAvatar(workspaceRequest.getAvatar());
        space.setStatus(true);
        space.setCreatedDate(workspaceRequest.getCreateTime());
        space.setUpdatedDate(workspaceRequest.getUpdateTime());
        space.setDeletedDate(workspaceRequest.getDeleteTime());

        return ResponseEntity.ok().body(workspaceService.createWorkspace(space));
    }

    @Operation(summary = "Update Workspace", description = "Update Workspace")
    @PutMapping("/{workspace_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Update Workspace"),
            @ApiResponse(responseCode = "402", description = "Unauthorized")
    })
    public ResponseEntity<Workspace> updateWorkspace(@RequestBody WorkspaceRequest workspaceRequest, @PathVariable("workspace_id") UUID id){
        Optional<Workspace> space = workspaceService.findById(id);
        space.get().setName(workspaceRequest.getName());
        space.get().setAvatar(workspaceRequest.getAvatar());
        space.get().setStatus(true);
        space.get().setCreatedDate(workspaceRequest.getCreateTime());
        space.get().setUpdatedDate(workspaceRequest.getUpdateTime());
        space.get().setDeletedDate(workspaceRequest.getDeleteTime());

        return ResponseEntity.ok().body(workspaceService.updateWorkspace(space.get()));
    }

    @Operation(summary = "Delete Workspace", description = "Delete Workspace")
    @DeleteMapping("/{workspace_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Delete Workspace"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    public ResponseEntity<?> deleteWorkspace(@PathVariable("workspace_id") UUID id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.ok().body("Workspace has been deleted successfully.");
    }

    @Operation(summary = "Find workspace by keywords", description = "Find workspace by keywords")
    @GetMapping("/{key}")
    @ApiResponses({
            @ApiResponse(responseCode = "203", description = "Find workspace by keywords"),
            @ApiResponse(responseCode = "405", description = "Not find workspace")
    })
    public ResponseEntity<Workspace> findByKeyWorks(@PathVariable("key") String key){
        return ResponseEntity.ok().body(workspaceService.searchWorkspace(key));
    }

    @Operation(summary = "Get all data workspace", description = "Get all data workspace")
    @GetMapping("/")
    @ApiResponses({
            @ApiResponse(responseCode = "209", description = "Get all data workspace"),
            @ApiResponse(responseCode = "411", description = "Not find member")
    })
    public ResponseEntity<List<Workspace>> getAllWorkspace(){
        return ResponseEntity.ok().body(workspaceService.getAllWorkspace());
    }

    @Operation(summary = "Add Workspace member", description = "Add Workspace member")
    @PostMapping("/member/add")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Add Workspace member"),
            @ApiResponse(responseCode = "406", description = "Unauthorized")
    })
    public ResponseEntity<WorkspaceMember> addWorkspaceMember(@Valid @RequestBody WorkspaceMemberRequest workspaceMemberRequest){
        WorkspaceMember space = new WorkspaceMember();
        SecurityUtils securityUtils = new SecurityUtils();
        space.setWorkspace(workspaceMemberRequest.getWorkspace());
        space.setMemberID(UUID.fromString(securityUtils.getCurrentUser()));

//        var code = verificationCodeService.generateVerificationCode(verifyRequest.getEmail());
//        kafkaProducer.sendEmailAddMemberWorkspace(Code.builder()
//                .verificationCode(code)
//                .email(verifyRequest.getEmail())
//                .verificationType(Code.VerificationType.FORGOT_PASSWORD)
//                .build()
//        );

        return ResponseEntity.ok().body(workspaceMemberService.addMember(space));
    }

    @Operation(summary = "Update Workspace member", description = "Update Workspace member")
    @PutMapping("/member/{workspacemember_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "205", description = "Update Workspace member"),
            @ApiResponse(responseCode = "407", description = "Unauthorized")
    })
    public ResponseEntity<WorkspaceMember> updateWorkspaceMember(@RequestBody WorkspaceMemberRequest workspaceMemberRequest, @PathVariable("workspacemember_id") UUID id){
        Optional<WorkspaceMember> space = workspaceMemberService.findMemberById(id);
        SecurityUtils securityUtils = new SecurityUtils();
        space.get().setWorkspace(workspaceMemberRequest.getWorkspace());
        space.get().setMemberID(UUID.fromString(securityUtils.getCurrentUser()));

        return ResponseEntity.ok().body(workspaceMemberService.updateMember(space.get()));
    }

    @Operation(summary = "Delete Workspace member", description = "Delete Workspace member")
    @DeleteMapping("member/{workspacemember_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "206", description = "Delete Workspace"),
            @ApiResponse(responseCode = "408", description = "Unauthorized")
    })
    public ResponseEntity<?> deleteWorkspaceMember(@PathVariable("workspacemember_id") UUID id) {
        workspaceMemberService.deleteMember(id);
        return ResponseEntity.ok().body("Workspace member has been deleted successfully.");
    }

    @Operation(summary = "Find workspace member by id", description = "Find workspace member by id")
    @GetMapping("/member/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "207", description = "Find workspace member by id"),
            @ApiResponse(responseCode = "409", description = "Not find workspace")
    })
    public ResponseEntity<Optional<WorkspaceMember>> findMemberById(@PathVariable("id") UUID id){
        return ResponseEntity.ok().body(workspaceMemberService.findMemberById(id));
    }

    @Operation(summary = "Get all data workspace member", description = "Get all data workspace member")
    @GetMapping("/member")
    @ApiResponses({
            @ApiResponse(responseCode = "208", description = "Get all data workspace member"),
            @ApiResponse(responseCode = "410", description = "Not find member")
    })
    public ResponseEntity<List<WorkspaceMember>> getAllWorkspaceMember(){
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
        role.setUserId(UUID.fromString(securityUtils.getCurrentUser()));

        return ResponseEntity.ok().body(roleWorkspaceService.addRoleWorkspace(role));
    }

    @Operation(summary = "Update Role Workspace", description = "Update Role Workspace")
    @PutMapping("/role/{role_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "211", description = "Update Role Workspace"),
            @ApiResponse(responseCode = "413", description = "Unauthorized")
    })
    public ResponseEntity<RoleWorkSpace> updateRole(@RequestBody RoleWorkspaceRequest roleWorkspaceRequest, @PathVariable("role_id") UUID id){
        Optional<RoleWorkSpace> role = roleWorkspaceService.findRoleWorkspaceById(id);
        SecurityUtils securityUtils = new SecurityUtils();
        role.get().setName(roleWorkspaceRequest.getName());
        role.get().setDescription(roleWorkspaceRequest.getDescription());
        role.get().setUserId(UUID.fromString(securityUtils.getCurrentUser()));

        return ResponseEntity.ok().body(roleWorkspaceService.updateRoleWorkspace(role.get()));
    }

    @Operation(summary = "Delete Role Workspace", description = "Delete Role Workspace")
    @DeleteMapping("role/{role_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "212", description = "Delete Role Workspace"),
            @ApiResponse(responseCode = "414", description = "Unauthorized")
    })
    public ResponseEntity<?> deleteRole(@PathVariable("role_id") UUID id) {
        roleWorkspaceService.deleteRoleWorkspace(id);
        return ResponseEntity.ok().body("Role Workspace has been deleted successfully.");
    }

    @Operation(summary = "Find role workspace by id", description = "Find role workspace by id")
    @GetMapping("/role/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "213", description = "Find role workspace by id"),
            @ApiResponse(responseCode = "415", description = "Not find workspace")
    })
    public ResponseEntity<Optional<RoleWorkSpace>> findRoleById(@PathVariable("id") UUID id){
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
