package com.octopus.dtomodels;

import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceDTO {
    private String id;
    private String name;
    private boolean status;
    private String avatar;
    private Date createdDate;
    private Date updatedDate;
    private Date deletedDate;
    private Set<UserDTO> members;
    private Set<ProjectDTO> projects;
}