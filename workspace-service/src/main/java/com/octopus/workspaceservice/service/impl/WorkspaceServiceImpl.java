package com.octopus.workspaceservice.service.impl;

import com.octopus.dtomodels.*;
import com.octopus.workspaceservice.Utils;
import com.octopus.workspaceservice.dtos.request.*;
import com.octopus.workspaceservice.dtos.response.ProjectAddResponse;
import com.octopus.workspaceservice.kafka.KafkaProducer;
import com.octopus.workspaceservice.mapper.WorkspaceMapper;
import com.octopus.workspaceservice.models.*;
import com.octopus.workspaceservice.repository.*;
import com.octopus.workspaceservice.service.WorkspaceService;
import com.octopus.workspaceservice.specification.WorkspaceSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.TypedParameterValue;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final ProjectRepository projectRepository;
    private final SettingRepository settingRepository;
    private final SpaceRepository spaceRepository;
    private final TaskRepository taskRepository;
    private final WorkspaceMapper workspaceMapper;
    private final WebClient.Builder webClientBuilder;
    private final KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public WorkspaceDTO createNewWorkspace(WorkspaceRequest workspaceRequest, String token, String userID) {
        var ownerRole = Utils.defaultOwnerRole();
        var workspace = Workspace.builder().name(workspaceRequest.getName()).status(true).createdBy(userID).workspaceRoles(Set.of(ownerRole, Utils.defaultGuestRole(), Utils.defaultMemberRole())).build();
        workspace.addMember(WorkspaceMember.builder().memberID(userID).workspace(workspace).workspaceRoleID(ownerRole.getId()).build());
        var newWorkspace = this.workspaceRepository.save(workspace);
        return convertToWorkspaceDTO(newWorkspace, token);
    }

    @Override
    @Transactional
    public WorkspaceMemberDTO addMember(String workspaceID, AddMembersRequest members, String token, String userID) {
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var adder = this.findUserByID(userID, token);
        var user = createUserTemp(members.getEmail(), token);
        var workspaceMember = WorkspaceMember.builder().memberID(user.getId()).workspace(workspace).workspaceRoleID(UUID.fromString(members.getRole())).build();
        if (members.getGroup() != null) {
            workspaceMember.addGroup(members.getGroup());
        }
        var newWorkspaceMember = this.workspaceMemberRepository.save(workspaceMember);
        kafkaProducer.sendEmailAddMemberWorkspace(Code.builder().email(members.getEmail()).name(adder.getFirstName() + " " + adder.getLastName()).workspaceName(workspace.getName()).build());
        return convertToWorkspaceMemberDTO(newWorkspaceMember, workspace, token);
    }

    @Override
    public WorkspaceDTO addRole(String workspaceID, AddRoleRequest roleRequest, String token) {
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var role = WorkspaceRole.builder()
                .id(UUID.randomUUID())
                .name(roleRequest.getName())
                .description(roleRequest.getDescription())
                .ownCapabilities(roleRequest.getOwnCapabilities())
                .build();
        workspace.addRole(role);
        var newWorkspace = this.workspaceRepository.save(workspace);
        return convertToWorkspaceDTO(newWorkspace, token);
    }

    @Override
    public WorkspaceDTO addGroup(String workspaceID, AddGroupRequest groupRequest, String token) {
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var groupID = UUID.randomUUID();
        var group = WorkspaceGroup.builder()
                .id(groupID)
                .name(groupRequest.getName())
                .description(groupRequest.getDescription())
                .build();
        workspace.addGroup(group);
        if (groupRequest.getMemberID() != null) {
            workspace.getMembers().forEach(member -> {
                if (groupRequest.getMemberID().contains(member.getMemberID())) {
                    member.addGroup(groupID.toString());
                }
            });
        }
        var newWorkspace = this.workspaceRepository.save(workspace);
        return convertToWorkspaceDTO(newWorkspace, token);
    }

    private UserDTO findUserByID(String id, String token) {
        return webClientBuilder.build().get().uri("http://auth-service/api/users", uriBuilder -> uriBuilder.path("/{id}").build(id)).header("Authorization", token).accept(MediaType.APPLICATION_JSON).retrieve().bodyToFlux(UserDTO.class).blockFirst();
    }

    private UserDTO createUserTemp(String email, String token) {
        return webClientBuilder.build().post().uri("http://auth-service/api/users/createUserTemp").body(Mono.just(CreateMemberTempRequest.builder().email(email).build()), CreateMemberTempRequest.class).header("Authorization", token).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(UserDTO.class).block();
    }

    @Override
    public List<WorkspaceDTO> searchWorkspace(Payload payload, String token) {
        var spec = new WorkspaceSpecification(payload);
        var pageable = WorkspaceSpecification.getPageable(payload.getPage(), payload.getSize());
        var workspaces = this.workspaceRepository.findAll(spec, pageable);
        return workspaces.stream().map(workspace -> convertToWorkspaceDTO(workspace, token)).collect(Collectors.toList());
    }

    @Override
    public List<WorkspaceDTO> getAllWorkspace() {
        var workspaces = this.workspaceRepository.findAllWorkspace();
        return workspaceMapper.mapListWorkspaceToWorkspaceDTO(workspaces);
    }

    @Override
    public List<WorkspaceDTO> getWorkspaceByUser(String userID, String token) {
        var workspaces = this.workspaceRepository.findWorkspacesByMemberID(userID);
        return workspaces.stream().map(workspace -> convertToWorkspaceDTO(workspace, token)).collect(Collectors.toList());
    }

    @Override
    public WorkspaceDTO findWorkspaceByID(String id, String token) {
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(id));
        return convertToWorkspaceDTO(workspace, token);
    }

    @Override
    @Transactional
    public WorkspaceDTO createProject(String workspaceID, ProjectRequest projectRequest, String token, String userID) {
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var setting = Setting.builder().taskStatuses(projectRequest.getStatusList()).build();
        var newSetting = this.settingRepository.save(setting);
        var newProject = Project.builder()
                .id(UUID.randomUUID())
                .name(projectRequest.getName())
                .avatar(projectRequest.getAvatar())
                .status(true)
                .setting(newSetting)
                .workspaceAccess(projectRequest.getWorkspaceAccess())
                .build();

        if (projectRequest.getMembers() != null) {
            for (var member : projectRequest.getMembers()) {
                if (member.equals(userID)) {
                    newProject.addMember(ProjectMember.builder().memberID(member).project(newProject).role(ProjectRole.OWNER).build());
                } else {
                    newProject.addMember(ProjectMember.builder().memberID(member).project(newProject).role(ProjectRole.MEMBER).build());
                }
            }
        }

        workspace.getProjects().add(newProject);
        var updatedWorkspace = this.workspaceRepository.save(workspace);
        if (projectRequest.getCreateChannel()) {
            var createChannelRequest = CreateChannelRequest.builder().name(projectRequest.getName()).newMembers(projectRequest.getMembers()).userID(userID).build();
            createChannel(createChannelRequest, token);
        }
        return convertToWorkspaceDTO(updatedWorkspace, token);
    }

    private void createChannel(CreateChannelRequest createChannelRequest, String token) {
        webClientBuilder.build().post().uri("http://message-service/api/channels").body(Mono.just(createChannelRequest), CreateChannelRequest.class).header("Authorization", token).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(ChannelDTO.class).block();
    }

    @Override
    public ProjectDTO createSpace(String workspaceID, String projectID, AddSpaceRequest addSpaceRequest, String token) {
        var project = this.projectRepository.findById(UUID.fromString(projectID)).get();
        var space = Space.builder().name(addSpaceRequest.getName()).status(true).setting(addSpaceRequest.getSetting()).build();
        project.getSpaces().add(space);
        var updatedProject = this.projectRepository.save(project);
        var projectDTO = this.workspaceMapper.mapToProjectDTO(updatedProject);
        projectDTO.getMembers().forEach(member -> {
            member.setUser(findUserByID(member.getMemberID(), token));
        });
        return projectDTO;
    }

    @Override
    public ProjectDTO addTask(String projectID, String spaceID, AddTaskRequest taskRequest, String token) {
        var space = this.spaceRepository.findById(UUID.fromString(spaceID)).get();
        var id = UUID.randomUUID();
        log.info(id.toString());
        var task = Task.builder().name(taskRequest.getName()).description(taskRequest.getDescription()).status(true).taskStatus(taskRequest.getTaskStatus()).assignees(taskRequest.getAssignees()).startDate(taskRequest.getStartDate()).dueDate(taskRequest.getDueDate()).build();
        space.getTasks().add(task);
        this.spaceRepository.save(space);
        var project = this.projectRepository.findById(UUID.fromString(projectID)).get();
        var projectDTO = this.workspaceMapper.mapToProjectDTO(project);
        projectDTO.getMembers().forEach(member -> {
            member.setUser(findUserByID(member.getMemberID(), token));
        });
        return projectDTO;
    }

    @Override
    public ProjectDTO updateTask(TaskDTO taskDTO, String token) {
        var task = this.workspaceMapper.mapTaskDTOToTask(taskDTO);
        var updatedTask = this.taskRepository.save(task);
        var space = this.spaceRepository.findSpaceByTask(updatedTask.getId());
        var project = this.projectRepository.findProjectBySpace(space.getId());
        var projectDTO = this.workspaceMapper.mapToProjectDTO(project);
        projectDTO.getMembers().forEach(member -> {
            member.setUser(findUserByID(member.getMemberID(), token));
        });
        return projectDTO;
    }

    @Override
    public GetTaskResponse getTaskToday(String workspaceID, String token) {
        var task = this.taskRepository.findTaskToday(UUID.fromString(workspaceID));
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var taskDTO = this.workspaceMapper.mapTaskListToTaskDTO(task);
        return new GetTaskResponse(taskDTO, convertToWorkspaceDTO(workspace, token));
    }

    @Override
    public GetTaskResponse getTaskExpirationDate(String workspaceID, String token) {
        var task = this.taskRepository.findTaskExpirationDate(UUID.fromString(workspaceID));
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var taskDTO = this.workspaceMapper.mapTaskListToTaskDTO(task);
        return new GetTaskResponse(taskDTO, convertToWorkspaceDTO(workspace, token));
    }

    @Override
    public GetTaskResponse getTaskNotStartDay(String workspaceID, String token) {
        var task = this.taskRepository.findTaskNotStartDay(UUID.fromString(workspaceID));
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var taskDTO = this.workspaceMapper.mapTaskListToTaskDTO(task);
        return new GetTaskResponse(taskDTO, convertToWorkspaceDTO(workspace, token));
    }

    @Override
    public GetTaskResponse getTaskDateInTerm(String workspaceID, String token) {
        var task = this.taskRepository.findTaskDateInTerm(UUID.fromString(workspaceID));
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var taskDTO = this.workspaceMapper.mapTaskListToTaskDTO(task);
        return new GetTaskResponse(taskDTO, convertToWorkspaceDTO(workspace, token));
    }

    @Override
    public GetTaskResponse getTaskNotDueDate(String workspaceID, String token) {
        var task = this.taskRepository.findTaskNotDueDate(UUID.fromString(workspaceID));
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var taskDTO = this.workspaceMapper.mapTaskListToTaskDTO(task);
        return new GetTaskResponse(taskDTO, convertToWorkspaceDTO(workspace, token));
    }

    @Override
    public GetTaskResponse getTaskDone(String workspaceID, String token) {
        var task = this.taskRepository.findTaskDone(UUID.fromString(workspaceID));
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var taskDTO = this.workspaceMapper.mapTaskListToTaskDTO(task);
        return new GetTaskResponse(taskDTO, convertToWorkspaceDTO(workspace, token));
    }

    @Override
    public ProjectDTO deleteTask(String projectID, String taskID, String token) {
        this.taskRepository.deleteById(UUID.fromString(taskID));
        var project = this.projectRepository.findById(UUID.fromString(projectID)).get();
        return this.workspaceMapper.mapToProjectDTO(project);
    }

    @Override
    public ProjectDTO deleteSpace(String projectID, String spaceID, String token) {
        this.spaceRepository.deleteById(UUID.fromString(spaceID));
        var project = this.projectRepository.findById(UUID.fromString(projectID)).get();
        return this.workspaceMapper.mapToProjectDTO(project);
    }

    private WorkspaceDTO convertToWorkspaceDTO(Workspace workspace, String token) {
        var workspaceDTO = workspaceMapper.mapToWorkspaceDTO(workspace);
        var members = workspace.getMembers().stream().map(workspaceMember ->
                        convertToWorkspaceMemberDTO(workspaceMember, workspace, token))
                .collect(Collectors.toSet());
        if (workspaceDTO.getProjects() != null) {
            workspaceDTO.getProjects().forEach(projectDTO -> {
                if (projectDTO.getMembers() != null) {
                    projectDTO.getMembers().forEach(member -> {
                        member.setUser(findUserByID(member.getMemberID(), token));
                    });
                }
            });
        }
        workspaceDTO.setMembers(members);
        return workspaceDTO;
    }

    private WorkspaceMemberDTO convertToWorkspaceMemberDTO(WorkspaceMember workspaceMember, Workspace workspace, String token) {
        return WorkspaceMemberDTO.builder()
                .user(findUserByID(workspaceMember.getMemberID(), token))
                .createdDate(workspaceMember.getCreatedDate())
                .updatedDate(workspaceMember.getUpdatedDate())
                .role(workspaceMapper.mapToWorkspaceRoleDTO(workspace.getWorkspaceRoles().stream().filter(workspaceRole -> workspaceRole.getId().equals(workspaceMember.getWorkspaceRoleID()))
                        .findFirst().orElse(null)))
                .groups(workspace.getWorkspaceGroups() != null ? workspaceMapper.mapListWorkspaceGroupToWorkspaceGroupDTO(workspace.getWorkspaceGroups().stream().filter(workspaceGroup -> {
                    if (workspaceMember.getGroups() != null) {
                        return workspaceMember.getGroups().contains(workspaceGroup.getId().toString());
                    }
                    return false;
                }).collect(Collectors.toSet())) : null)
                .build();
    }

    //    @Override
//    public Workspace createWorkspace(Workspace workSpace) {
//        return this.workspaceRepository.save(workSpace);
//    }
//
//    @Override
//    public void deleteWorkspace(UUID id) {
//        this.workspaceRepository.findById(id).get().setStatus(false);
//        //this.workspaceRepository.deleteById(id);
//    }
//
//    @Override
//    public Workspace updateWorkspace(Workspace workSpace) {
//        return this.workspaceRepository.save(workSpace);
//    }
//
//    @Override
//    public Workspace searchWorkspace(String key) {
//        return this.workspaceRepository.fillWorkspaceByKeywords(key);
//    }
//
//    @Override
//    public Optional<Workspace> findById(UUID id) {
//        return this.workspaceRepository.findById(id);
//    }
//
//
//    @Override
//    public List<WorkspaceDTO> search(Payload payload) {
//        var spec = new WorkspaceSpecification(payload);
//        var pageable = WorkspaceSpecification.getPageable(payload.getPage(), payload.getSize());
//        var workspaces = workspaceRepository.findAll(spec, pageable).getContent();
//        return workspaceMapper.mapListWorkspaceToWorkspaceDTO(workspaces);
//    }


}
