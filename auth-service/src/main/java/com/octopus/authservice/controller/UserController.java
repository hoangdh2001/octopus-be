package com.octopus.authservice.controller;

import com.octopus.authservice.dto.request.ChangePasswordRequest;
import com.octopus.authservice.dto.request.DeviceRequest;
import com.octopus.authservice.mapper.UserMapper;
import com.octopus.authservice.model.Device;
import com.octopus.authservice.model.User;
import com.octopus.authservice.service.UserService;
import com.octopus.dtomodels.DeviceDTO;
import com.octopus.dtomodels.OwnUserDTO;
import com.octopus.dtomodels.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = this.userService.findAll();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserByID(@PathVariable("id") String id) {
        var user = this.userService.findUserById(id);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserByID(@PathVariable("id") String id, @RequestBody UserDTO userDTO) {
        var user = this.userService.updateUserByID(id, userDTO);
        return ResponseEntity.ok().body(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> changePassword(@PathVariable("id") String id, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        var user = this.userService.changePassword(id, changePasswordRequest);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/{id}/me")
    public ResponseEntity<OwnUserDTO> getMyInfo(@PathVariable("id") String id) {
        var ownUser = this.userService.getMyInfo(id);
        return ResponseEntity.ok().body(ownUser);
    }

    @PutMapping("/{id}/me")
    public ResponseEntity<OwnUserDTO> updateMyInfo(@PathVariable("id") String id, @RequestBody OwnUserDTO ownUserDTO) {
        var ownUser = this.userService.updateMyInfo(id, ownUserDTO);
        return ResponseEntity.ok().body(ownUser);
    }

    @GetMapping("/{id}/devices")
    public ResponseEntity<List<DeviceDTO>> getDevicesByUser(@PathVariable("id") String userID) {
        var devices = this.userService.findDevicesByUserId(userID);
        return ResponseEntity.ok().body(devices);
    }

    @PostMapping("/{id}/devices")
    public ResponseEntity<Void> addDevices(@Valid @RequestBody DeviceRequest request, @PathVariable String id) {
        var device = this.userService.addDevice(request, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/devices")
    public ResponseEntity<Void> deleteDevice(@Param("id") String deviceID, @PathVariable String id) {
        var success = this.userService.removeDevice(deviceID);
        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(410).build();
        }
    }

}
