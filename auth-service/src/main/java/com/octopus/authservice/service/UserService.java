package com.octopus.authservice.service;


import com.octopus.authservice.dto.request.ChangePasswordRequest;
import com.octopus.authservice.dto.request.DeviceRequest;
import com.octopus.authservice.dto.request.SignupRequest;
import com.octopus.authservice.model.Device;
import com.octopus.authservice.model.User;
import com.octopus.dtomodels.DeviceDTO;
import com.octopus.dtomodels.OwnUserDTO;
import com.octopus.dtomodels.Payload;
import com.octopus.dtomodels.UserDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDTO> findAll();

    UserDTO findUserById(String id);

    UserDTO updateUserByID(String id, UserDTO other);
    Boolean existByEmailAndEnableIsTrue(String email);

    UserDTO findUserByEmail(String email);

    UserDTO createUserTemp(String email);

    Boolean existByEmail(String email);

    UserDTO createUser(SignupRequest signupRequest);

    UserDTO changePassword(String id, ChangePasswordRequest changePasswordRequest);

    UserDTO resetPassword(String email, String password);

    List<DeviceDTO> findDevicesByUserId(String userID);

    DeviceDTO addDevice(DeviceRequest device, String userID);

    boolean removeDevice(String id);

    OwnUserDTO getMyInfo(String id);

    OwnUserDTO updateMyInfo(String id, OwnUserDTO ownUserDTO);

    List<OwnUserDTO> getOwners(String[] users);

    List<UserDTO> searchUser(Payload payload);
}

