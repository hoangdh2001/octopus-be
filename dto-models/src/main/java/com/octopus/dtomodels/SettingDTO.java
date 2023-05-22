package com.octopus.dtomodels;

import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingDTO {
    private String id;
    private Set<TaskStatusDTO> statuses;

    private Date createdDate;
    private Date updatedDate;
}
