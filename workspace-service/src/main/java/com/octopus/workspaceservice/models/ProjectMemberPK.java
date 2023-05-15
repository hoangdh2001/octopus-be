package com.octopus.workspaceservice.models;

import lombok.*;

import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class ProjectMemberPK {
    private UUID project;
    private UUID memberID;
    private UUID workmember;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectMemberPK that = (ProjectMemberPK) o;
        return Objects.equals(project, that.project) && Objects.equals(memberID, that.memberID) && Objects.equals(workmember, that.workmember);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, memberID, workmember);
    }
}
