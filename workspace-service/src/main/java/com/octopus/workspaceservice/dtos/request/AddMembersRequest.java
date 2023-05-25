package com.octopus.workspaceservice.dtos.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddMembersRequest {
    private List<String> members;
    private String email;
}
