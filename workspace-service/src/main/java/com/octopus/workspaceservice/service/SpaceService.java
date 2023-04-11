package com.octopus.workspaceservice.service;

import com.octopus.workspaceservice.model.Project;
import com.octopus.workspaceservice.model.Space;

import java.util.Optional;

public interface SpaceService {
    public static final int SPACE_PER_PAGE = 4;
    public Space createSpace(Space space);
    public void deleteProject(int id);
    public Space updateProject(Space space);
    public Optional<Space> findById(int id);
    public Space searchProject(String key);
}
