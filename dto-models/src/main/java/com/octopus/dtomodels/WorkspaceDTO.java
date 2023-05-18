package com.octopus.dtomodels;

import lombok.*;

import java.util.Date;
import java.util.List;

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
    private List<UserDTO> members;
    private List<ProjectDTO> projects;
}
