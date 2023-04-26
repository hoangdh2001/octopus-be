package com.octopus.authservice.controller;

import com.octopus.authservice.dto.request.ChangePasswordRequest;
import com.octopus.authservice.dto.request.DeviceRequest;
import com.octopus.authservice.mapper.UserMapper;
import com.octopus.authservice.model.Device;
import com.octopus.authservice.model.User;
import com.octopus.authservice.service.UserService;
import com.octopus.dtomodels.DeviceDTO;
import com.octopus.dtomodels.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@Slf4j
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
    public ResponseEntity<UserDTO> updateUserByID(@PathVariable("id") String id, @RequestBody UserDTO userDTO) {
        User user = this.userService.updateUserByID(id, userMapper.mapUserDTOToUser(userDTO));
        return ResponseEntity.ok().body(userMapper.mapToUserDTO(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> changePassword(@PathVariable("id") String id, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        User user = this.userService.changePassword(id, changePasswordRequest);
        return ResponseEntity.ok().body(userMapper.mapToUserDTO(user));
    }

    @GetMapping("/{id}/devices")
    public ResponseEntity<List<DeviceDTO>> getDevicesByUser(@PathVariable("id") String userID) {
        List<Device> devices = this.userService.findDevicesByUserId(userID);
        return ResponseEntity.ok().body(userMapper.mapListDeviceToDeviceDTO(devices));
    }

    @PostMapping("/{id}/devices")
    public ResponseEntity<Void> addDevices(@Valid @RequestBody DeviceRequest request, @PathVariable String id) {
        Device device = this.userService.addDevice(request, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/devices")
    public ResponseEntity<Void> deleteDevice(@Param("id") String deviceID, @PathVariable String id) {
        boolean success = this.userService.removeDevice(deviceID);
        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(410).build();
        }
    }

}
