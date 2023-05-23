package com.octopus.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octopus.authservice.dto.request.ChangePasswordRequest;
import com.octopus.authservice.dto.request.DeviceRequest;
import com.octopus.authservice.service.UserService;
import com.octopus.dtomodels.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/ownUsers")
    public ResponseEntity<List<OwnUserDTO>> getOwners(@RequestParam("users") String users) {
        String[] userIDs = users.split(",");
        var owners = this.userService.getOwners(userIDs);
        return ResponseEntity.ok().body(owners);
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

    @SneakyThrows
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUser(@RequestParam("payload") String payloadString) {
        log.info(payloadString);
        ObjectMapper objectMapper = new ObjectMapper();
        var payload = objectMapper.readValue(payloadString, Payload.class);
        var users = this.userService.searchUser(payload);
        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/createUserTemp")
    public ResponseEntity<UserDTO> createUserTemp(@RequestBody() CreateMemberTempRequest userDTO) {
        if (this.userService.existByEmail(userDTO.getEmail())) {
            var user = this.userService.findUserByEmail(userDTO.getEmail());
            return ResponseEntity.ok().body(user);
        }
        var user = this.userService.createUserTemp(userDTO.getEmail());
        return ResponseEntity.ok().body(user);
    }

}
