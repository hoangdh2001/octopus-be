package com.octopus.dtomodels;

import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
    private String id;
    private String name;
    private boolean status;
    private String avatar;
    private Date createdDate;
    private Date updatedDate;
    private Date deletedDate;
    private Set<SpaceDTO> spaces;
    private SettingDTO setting;
    private Set<ProjectMemberDTO> members;
}
