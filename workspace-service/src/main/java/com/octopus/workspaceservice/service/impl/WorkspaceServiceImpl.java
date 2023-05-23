package com.octopus.workspaceservice.service.impl;

import com.octopus.dtomodels.*;
import com.octopus.workspaceservice.dtos.request.AddSpaceRequest;
import com.octopus.workspaceservice.dtos.request.AddTaskRequest;
import com.octopus.workspaceservice.dtos.request.ProjectRequest;
import com.octopus.workspaceservice.dtos.request.WorkspaceRequest;
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
    private final ProjectRepository projectRepository;
    private final SettingRepository settingRepository;
    private final SpaceRepository spaceRepository;
    private final TaskRepository taskRepository;
    private final WorkspaceMapper workspaceMapper;
    private final WebClient.Builder webClientBuilder;

    @Override
    @Transactional
    public WorkspaceDTO createNewWorkspace(WorkspaceRequest workspaceRequest, String token) {
        var newWorkspace = Workspace.builder()
                .name(workspaceRequest.getName())
                .status(true)
                .build();
        if (workspaceRequest.getMembers() != null) {
            var workspaceMembers = workspaceRequest.getMembers().stream().map(s -> WorkspaceMember.builder()
                    .memberID(s)
                    .workspace(newWorkspace)
                    .build()).collect(Collectors.toSet());
            newWorkspace.setMembers(workspaceMembers);
        }
        var workspace = this.workspaceRepository.save(newWorkspace);
        var workspaceDTO = this.workspaceMapper.mapToWorkspaceDTO(workspace);
        var members = workspace.getMembers().stream().map(workspaceMember -> findUserByID(workspaceMember.getMemberID().toString(), token)).collect(Collectors.toSet());
        workspaceDTO.setMembers(members);
        return workspaceDTO;
    }



    @Override
    @Transactional
    public Set<UserDTO> addMembers(String workspaceID, List<String> members, String token) {
        var workspace = Workspace.builder()
                .id(UUID.fromString(workspaceID))
                .build();
        var workspaceMembers = members.stream().map(s -> WorkspaceMember.builder()
                .memberID(s)
                .workspace(workspace)
                .build()).collect(Collectors.toSet());
        this.workspaceMemberRepository.saveAll(workspaceMembers);
        return workspaceMembers.stream().map(workspaceMember -> findUserByID(workspaceMember.getMemberID().toString(), token)).collect(Collectors.toSet());
    }

    private UserDTO findUserByID(String id, String token) {
        return webClientBuilder
                .build()
                .get().uri("http://auth-service/api/users", uriBuilder -> uriBuilder.path("/{id}").build(id))
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(UserDTO.class)
                .blockFirst();
    }

    @Override
    public List<WorkspaceDTO> searchWorkspace(Payload payload, String token) {
        var spec = new WorkspaceSpecification(payload);
        var pageable = WorkspaceSpecification.getPageable(payload.getPage(), payload.getSize());
        var workspaces = this.workspaceRepository.findAll(spec, pageable);
        var workspaceDTOList = workspaceMapper.mapListWorkspaceToWorkspaceDTO(workspaces.getContent());
        for (int i = 0; i < workspaces.getContent().size(); i++) {
            var workspace = workspaces.getContent().get(i);
            var workspaceDTO = workspaceDTOList.get(i);
            var members = workspace.getMembers().stream().map(workspaceMember -> findUserByID(workspaceMember.getMemberID().toString(), token)).collect(Collectors.toSet());
            workspaceDTO.setMembers(members);
        }
        return workspaceDTOList;
    }

    @Override
    public List<WorkspaceDTO> getAllWorkspace() {
        var workspaces = this.workspaceRepository.findAllWorkspace();
        return workspaceMapper.mapListWorkspaceToWorkspaceDTO(workspaces);
    }

    @Override
    public List<WorkspaceDTO> getWorkspaceByUser(String userID, String token) {
        var workspaces = this.workspaceRepository.findWorkspacesByMemberID(userID);
        var workspaceDTOList = workspaceMapper.mapListWorkspaceToWorkspaceDTO(workspaces);
        for (int i = 0; i < workspaces.size(); i++) {
            var workspace = workspaces.get(i);
            var workspaceDTO = workspaceDTOList.get(i);
            var members = workspace.getMembers().stream().map(workspaceMember -> findUserByID(workspaceMember.getMemberID().toString(), token)).collect(Collectors.toSet());
            workspaceDTO.setMembers(members);
        }
        return workspaceDTOList;
    }

    @Override
    public WorkspaceDTO findWorkspaceByID(String id, String token) {
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(id));
        var workspaceDTO = workspaceMapper.mapToWorkspaceDTO(workspace);
        var members = workspace.getMembers().stream().map(workspaceMember -> findUserByID(workspaceMember.getMemberID().toString(), token)).collect(Collectors.toSet());
        workspaceDTO.setMembers(members);
        return workspaceDTO;
    }

    @Override
    public WorkspaceDTO createProject(String workspaceID, ProjectRequest projectRequest, String token) {
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var setting = Setting.builder()
                .taskStatuses(projectRequest.getStatusList())
                .build();
        var newSetting = this.settingRepository.save(setting);
        var newProject = Project.builder()
                .name(projectRequest.getName())
                .avatar(projectRequest.getAvatar())
                .status(true)
                .setting(newSetting)
                .build();
        workspace.getProjects().add(newProject);
        var updatedWorkspace = this.workspaceRepository.saveAndFlush(workspace);
        var workspaceDTO = workspaceMapper.mapToWorkspaceDTO(updatedWorkspace);
        var members = workspace.getMembers().stream().map(workspaceMember -> findUserByID(workspaceMember.getMemberID(), token)).collect(Collectors.toSet());
        workspaceDTO.setMembers(members);
        return workspaceDTO;
    }

    @Override
    public ProjectDTO createSpace(String workspaceID, String projectID, AddSpaceRequest addSpaceRequest, String token) {
        var project = this.projectRepository.findById(UUID.fromString(projectID)).get();
        var space = Space.builder()
                .name(addSpaceRequest.getName())
                .status(true)
                .setting(addSpaceRequest.getSetting())
                .build();
        project.getSpaces().add(space);
        var updatedProject = this.projectRepository.save(project);
        return this.workspaceMapper.mapToProjectDTO(updatedProject);
    }

    @Override
    public ProjectDTO addTask(String projectID, String spaceID, AddTaskRequest taskRequest, String token) {
        var space = this.spaceRepository.findById(UUID.fromString(spaceID)).get();
        var id = UUID.randomUUID();
        log.info(id.toString());
        var task = Task.builder()
                .name(taskRequest.getName())
                .description(taskRequest.getDescription())
                .status(true)
                .taskStatus(taskRequest.getTaskStatus())
                .assignees(taskRequest.getAssignees())
                .startDate(taskRequest.getStartDate())
                .dueDate(taskRequest.getDueDate())
                .build();
        space.getTasks().add(task);
        this.spaceRepository.save(space);
        var project = this.projectRepository.findById(UUID.fromString(projectID)).get();
        return this.workspaceMapper.mapToProjectDTO(project);
    }

    @Override
    public ProjectDTO updateTask(TaskDTO taskDTO, String token) {
        var task = this.workspaceMapper.mapTaskDTOToTask(taskDTO);
        var updatedTask = this.taskRepository.save(task);
        var project = this.projectRepository.findById(UUID.fromString("043fc600-bd32-45b5-b3fe-35a1653bc1b8")).get();
        return this.workspaceMapper.mapToProjectDTO(project);
    }

    @Override
    public GetTaskResponse getTaskToday(String workspaceID, String token) {
        var task = this.taskRepository.findTaskToday(UUID.fromString(workspaceID));
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var workspaceDTO = this.workspaceMapper.mapToWorkspaceDTOIgnoreProject(workspace);
        var members = workspace.getMembers().stream().map(workspaceMember -> findUserByID(workspaceMember.getMemberID(), token)).collect(Collectors.toSet());
        workspaceDTO.setMembers(members);
        var taskDTO = this.workspaceMapper.mapTaskListToTaskDTO(task);
        return new GetTaskResponse(taskDTO, workspaceDTO);
    }

    @Override
    public GetTaskResponse getTaskExpirationDate(String workspaceID, String token) {
        var task = this.taskRepository.findTaskExpirationDate(UUID.fromString(workspaceID));
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var workspaceDTO = this.workspaceMapper.mapToWorkspaceDTOIgnoreProject(workspace);
        var members = workspace.getMembers().stream().map(workspaceMember -> findUserByID(workspaceMember.getMemberID(), token)).collect(Collectors.toSet());
        workspaceDTO.setMembers(members);
        var taskDTO = this.workspaceMapper.mapTaskListToTaskDTO(task);
        return new GetTaskResponse(taskDTO, workspaceDTO);
    }

    @Override
    public GetTaskResponse getTaskNotStartDay(String workspaceID, String token) {
        var task = this.taskRepository.findTaskNotStartDay(UUID.fromString(workspaceID));
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var workspaceDTO = this.workspaceMapper.mapToWorkspaceDTOIgnoreProject(workspace);
        var members = workspace.getMembers().stream().map(workspaceMember -> findUserByID(workspaceMember.getMemberID(), token)).collect(Collectors.toSet());
        workspaceDTO.setMembers(members);
        var taskDTO = this.workspaceMapper.mapTaskListToTaskDTO(task);
        return new GetTaskResponse(taskDTO, workspaceDTO);
    }

    @Override
    public GetTaskResponse getTaskDateInTerm(String workspaceID, String token) {
        var task = this.taskRepository.findTaskDateInTerm(UUID.fromString(workspaceID));
        var workspace = this.workspaceRepository.findWorkspaceById(UUID.fromString(workspaceID));
        var workspaceDTO = this.workspaceMapper.mapToWorkspaceDTOIgnoreProject(workspace);
        var members = workspace.getMembers().stream().map(workspaceMember -> findUserByID(workspaceMember.getMemberID(), token)).collect(Collectors.toSet());
        workspaceDTO.setMembers(members);
        var taskDTO = this.workspaceMapper.mapTaskListToTaskDTO(task);
        return new GetTaskResponse(taskDTO, workspaceDTO);
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
