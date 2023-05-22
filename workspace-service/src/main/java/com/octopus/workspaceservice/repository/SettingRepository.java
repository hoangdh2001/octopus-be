package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.models.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SettingRepository extends JpaRepository<Setting, UUID> {
}
