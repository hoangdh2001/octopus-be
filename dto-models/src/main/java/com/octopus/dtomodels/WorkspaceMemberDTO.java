package com.octopus.dtomodels;

import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceMemberDTO {
    private UserDTO user;
    private Date createdDate;
    private Date updatedDate;
    private WorkspaceRoleDTO role;
    private Set<WorkspaceGroupDTO> groups;
}
