package com.octopus.workspaceservice.service.impl;

import com.octopus.workspaceservice.model.Space;
import com.octopus.workspaceservice.repository.SpaceRepository;
import com.octopus.workspaceservice.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SpaceServiceImpl implements SpaceService {
    private final SpaceRepository spaceRepository;

    @Override
    public Space createSpace(Space space) {
        return this.spaceRepository.save(space);
    }

    @Override
    public void deleteSpace(int id) {
        this.spaceRepository.deleteById(id);
    }

    @Override
    public Space updateSpace(Space space) {
        return this.spaceRepository.save(space);
    }

    @Override
    public Optional<Space> findById(int id) {
        return this.spaceRepository.findById(id);
    }

    @Override
    public Space searchSpace(String key) {
        return this.spaceRepository.findByKeyword(key);
    }
}
