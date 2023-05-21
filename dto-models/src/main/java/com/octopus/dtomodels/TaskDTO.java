package com.octopus.dtomodels;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {
    private String id;
    private String name;
    private String description;
    private Date createdDate;
    private Date updatedDate;
    private Date deletedDate;
}
