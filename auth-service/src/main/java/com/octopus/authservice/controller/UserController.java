package com.octopus.authservice.controller;

import com.octopus.authservice.dto.request.ChangePasswordRequest;
import com.octopus.authservice.mapper.UserMapper;
import com.octopus.authservice.model.User;
import com.octopus.authservice.service.UserService;
import com.octopus.dtomodels.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        List<User> users = this.userService.findAll();
        return ResponseEntity.ok().body(userMapper.mapListUserToUserDTO(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserByID(@PathVariable("id") String id) {
        User user = this.userService.findUserById(id);
        return ResponseEntity.ok().body(userMapper.mapToUserDTO(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserByID(@PathVariable("id") String id, @Valid @RequestBody UserDTO userDTO) {
        User user = this.userService.updateUserByID(id, userDTO);
        return ResponseEntity.ok().body(userMapper.mapToUserDTO(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> changePassword(@PathVariable("id") String id, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        User user = this.userService.changePassword(id, changePasswordRequest);
        return ResponseEntity.ok().body(userMapper.mapToUserDTO(user));
    }

}
