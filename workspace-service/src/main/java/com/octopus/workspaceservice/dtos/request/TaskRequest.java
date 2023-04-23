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
public class TaskRequest {
    private String description;
    private boolean status;
    @CreatedDate
    private Date createTime;
    @CreatedDate
    private Date createdDate;
    @CreatedDate
    private Date updatedDate;
    @CreatedDate
    private Date deletedDate;
}
