package com.octopus.workspaceservice.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddMembersRequest {
    private List<String> members;
    private String email;
}
