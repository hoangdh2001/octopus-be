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
public class WorkspaceRequest {
    private String name;
    private boolean status;
    private String avatar;
    private Date createTime;
    private Date updateTime;
    private Date deleteTime;
}
