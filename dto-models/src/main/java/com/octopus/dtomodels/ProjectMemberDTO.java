package com.octopus.dtomodels;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProjectMemberDTO {
    private String memberID;
    private UserDTO user;
    private Date createdDate;
    private Date updatedDate;
    private ProjectRoleDTO role;
}
