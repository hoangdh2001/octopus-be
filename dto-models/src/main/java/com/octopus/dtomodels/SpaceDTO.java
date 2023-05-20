package com.octopus.dtomodels;

import lombok.*;

import java.util.Date;

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

}
