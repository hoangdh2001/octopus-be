package com.octopus.dtomodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceGroupDTO {
    private String id;
    private String name;
    private String description;
    private Date createdDate;
    private Date updatedDate;
    private Date deletedDate;
}
