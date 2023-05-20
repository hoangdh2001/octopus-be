package com.octopus.workspaceservice.models;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class WorkspaceMemberPK implements Serializable {
    private UUID workspace;
    private String memberID;

    public WorkspaceMemberPK(UUID workspace, String memberID) {
        this.workspace = workspace;
        this.memberID = memberID;
    }

    public WorkspaceMemberPK() {
    }

    public UUID getWorkspace() {
        return workspace;
    }

    public void setWorkspace(UUID workspace) {
        this.workspace = workspace;
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkspaceMemberPK that = (WorkspaceMemberPK) o;
        return Objects.equals(workspace, that.workspace) && Objects.equals(memberID, that.memberID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workspace, memberID);
    }
}
