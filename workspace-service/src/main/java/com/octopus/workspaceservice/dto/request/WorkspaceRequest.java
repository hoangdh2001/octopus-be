package com.octopus.workspaceservice.dto.request;

import com.octopus.workspaceservice.model.Project;
import com.octopus.workspaceservice.model.RoleWorkSpace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
