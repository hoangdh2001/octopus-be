package com.octopus.dtomodels;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusDTO {
    private String id;
    private Integer numOrder;
    private String color;
    private Date createdDate;
    private Date updatedDate;
    private Date deletedDate;
    private String name;
    private Boolean closeStatus;
}
