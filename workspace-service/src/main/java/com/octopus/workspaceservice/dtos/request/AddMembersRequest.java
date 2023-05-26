package com.octopus.workspaceservice.dtos.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddMembersRequest {
    private String email;
    private String role;
    private String group;
}
