package com.octopus.workspaceservice.dtos.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddSpaceRequest {
    private String name;
}
