package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.model.WorkSpace;
import com.octopus.workspaceservice.model.WorkSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkspaceMemberRepository extends JpaRepository<WorkSpaceMember, Integer> {
}
