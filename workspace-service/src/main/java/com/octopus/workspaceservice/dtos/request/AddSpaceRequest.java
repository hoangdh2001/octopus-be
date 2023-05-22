package com.octopus.workspaceservice.dtos.request;

import com.octopus.workspaceservice.models.Setting;
import com.octopus.workspaceservice.models.TaskStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddSpaceRequest {
    private String name;
    private Setting setting;
}
