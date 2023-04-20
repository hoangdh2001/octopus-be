package com.octopus.workspaceservice.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {
    private String name;
    private String key;
    private boolean status;
    private String avatar;
    private Date createTime;
    private Date updateTime;
    private Date deleteTime;
    private int workspaceId;
    private int memberId;
}
