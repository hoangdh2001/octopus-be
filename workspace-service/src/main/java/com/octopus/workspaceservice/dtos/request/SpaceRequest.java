package com.octopus.workspaceservice.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceRequest {
    private String name;
    private boolean status;
    @CreatedDate
    private Date createDate;
    @LastModifiedDate
    private Date updateDate;
    @LastModifiedDate
    private Date deletedDate;
}
