package com.octopus.dtomodels;

import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceDTO {
    private String id;
    private String name;
    private Boolean status;
    private Date createdDate;
    private Date updatedDate;
    private Date deletedDate;
    private Set<TaskDTO> tasks;
    private SettingDTO setting;
}
