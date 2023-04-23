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
public class ProjectRequest {
    private String name;
    private boolean status;
    private String avatar;
    @CreatedDate
    private Date createDate;
    @LastModifiedDate
    private Date updateDate;
    @LastModifiedDate
    private Date deleteDate;
    private int workspaceId;
    private int memberId;
}
