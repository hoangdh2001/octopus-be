package com.octopus.workspaceservice.dtos.request;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChannelRequest implements Serializable {
    private String name;
    private List<String> newMembers;
    private String userID;
}
