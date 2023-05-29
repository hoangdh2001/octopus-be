package com.octopus.workspaceservice;

import com.octopus.workspaceservice.models.ProjectRole;
import com.octopus.workspaceservice.models.WorkspaceOwnCapability;
import com.octopus.workspaceservice.models.WorkspaceRole;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Utils {

    public static Set<WorkspaceRole> defaultListWorkspaceRole() {
        return Set.of(defaultOwnerRole(), defaultGuestRole(), defaultMemberRole());
    }

    public static WorkspaceRole defaultOwnerRole() {
        return WorkspaceRole.builder()
                .id(UUID.randomUUID())
                .roleDefault(true)
                .ownCapabilities(Set.of(WorkspaceOwnCapability.ALL_CAPABILITIES))
                .name("Owner")
                .build();
    }

    public static WorkspaceRole defaultGuestRole() {
        return WorkspaceRole.builder()
                .id(UUID.randomUUID())
                .roleDefault(true)
                .name("Guest")
                .build();
    }

    public static WorkspaceRole defaultMemberRole() {
        return WorkspaceRole.builder()
                .id(UUID.randomUUID())
                .name("Member")
                .ownCapabilities(Set.of(WorkspaceOwnCapability.CREATE_PROJECT, WorkspaceOwnCapability.CREATE_LIST, WorkspaceOwnCapability.ADD_MEMBER))
                .build();
    }
}
