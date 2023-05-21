package com.octopus.workspaceservice.dtos.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddTaskRequest {
    private String name;
    private String description;
}
