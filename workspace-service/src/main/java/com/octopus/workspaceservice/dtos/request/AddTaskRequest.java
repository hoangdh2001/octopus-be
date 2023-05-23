package com.octopus.workspaceservice.dtos.request;

import com.octopus.workspaceservice.models.TaskStatus;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddTaskRequest {
    private String name;
    private String description;
    private Date startDate;
    private Date dueDate;
    private List<String> assignees;
    private TaskStatus taskStatus;
}
