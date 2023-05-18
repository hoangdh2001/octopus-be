package com.octopus.authservice.service;

import com.octopus.authservice.dto.request.ChangePasswordRequest;
import com.octopus.authservice.dto.request.DeviceRequest;
import com.octopus.authservice.dto.request.SignupRequest;
import com.octopus.authservice.mapper.UserMapper;
import com.octopus.authservice.model.Device;
import com.octopus.authservice.model.User;
import com.octopus.authservice.repositories.DeviceRepository;
import com.octopus.authservice.repositories.UserRepository;
import com.octopus.authservice.specification.UserSpecification;
import com.octopus.dtomodels.DeviceDTO;
import com.octopus.dtomodels.OwnUserDTO;
import com.octopus.dtomodels.Payload;
import com.octopus.dtomodels.UserDTO;
import com.octopus.exceptionutils.InvalidDataException;
import com.octopus.exceptionutils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final DeviceRepository deviceRepository;
    public final PasswordEncoder passwordEncoder;

    public final UserMapper userMapper;

    @Override
    @Transactional
    public List<UserDTO> findAll() {
        var users = this.userRepository.findAll();
        return userMapper.mapListUserToUserDTO(users);
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    @Transactional
    public UserDTO findUserById(String id) {
        var user = this.userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Not found user with id " + id));
        return userMapper.mapToUserDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUserByID(String id, UserDTO other) {
        var user = this.userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Not found user with id " + id));
        user.merge(userMapper.mapUserDTOToUser(other));
        return userMapper.mapToUserDTO(this.userRepository.save(user));
    }

    @Override
    @Transactional
    public Boolean existByEmailAndEnableIsTrue(String email) {
        return this.userRepository.existsByEmailIgnoreCaseAndEnabledIsTrue(email);
    }

    @Override
    @Cacheable(value = "user", key = "#email")
    @Transactional
    public UserDTO findUserByEmail(String email) {
        var user = this.userRepository.findUserByEmailIgnoreCase(email).orElseThrow(() -> new NotFoundException("Not found user with email " + email));
        return userMapper.mapToUserDTO(user);
    }

    @Override
    @Transactional
    public UserDTO createUserTemp(String email) {
        var user = User.builder()
                .email(email)
                .enabled(false)
                .build();
        return userMapper.mapToUserDTO(this.userRepository.save(user));
    }

    @Override
    @Transactional
    public Boolean existByEmail(String email) {
        return this.userRepository.existsByEmailIgnoreCase(email);
    }

    public UserDTO createUser(SignupRequest signupRequest) {
        var user = this.userRepository.findUserByEmailIgnoreCase(signupRequest.getEmail()).orElseThrow(() -> new NotFoundException("Not found user with email " + signupRequest.getEmail()));
        user.setEnabled(true);
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        return userMapper.mapToUserDTO(this.userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO changePassword(String id, ChangePasswordRequest changePasswordRequest) {
        var user = this.userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Not found user with id " + id));
        var isValid = passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword());
        if (isValid) {
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPass()));
            return userMapper.mapToUserDTO(this.userRepository.save(user));
        }
        throw new InvalidDataException("Current password not match");
    }

    @Override
    @Transactional
    public UserDTO resetPassword(String email, String password) {
        var user = this.findUserByEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }

    @Override
    @Transactional
    public List<DeviceDTO> findDevicesByUserId(String userID) {
        return userMapper.mapListDeviceToDeviceDTO(this.deviceRepository.findDevicesByUserIdAndDisabledIsFalse(UUID.fromString(userID)));
    }

    @Override
    @Transactional
    public DeviceDTO addDevice(DeviceRequest deviceRequest, String id) {
        var user = this.userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Not found user with id " + id));
        var device = Device.builder()
                .pushProvider(deviceRequest.getPushProvider())
                .deviceID(deviceRequest.getDeviceID())
                .user(user)
                .name(deviceRequest.getName())
                .build();
        return userMapper.mapToDeviceDTO(this.deviceRepository.save(device));
    }

    @Override
    @Transactional
    public boolean removeDevice(String id) {
        return this.deviceRepository.disabledDevice(id);
    }

    @Override
    @Transactional
    public OwnUserDTO getMyInfo(String id) {
        var user = this.userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Not found user with id " + id));
        return userMapper.mapToOwnUserDTO(user);
    }

    @Override
    public OwnUserDTO updateMyInfo(String id, OwnUserDTO ownUserDTO) {
        var user = this.userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Not found user with id " + id));
        user.merge(userMapper.mapToUser(ownUserDTO));
        return userMapper.mapToOwnUserDTO(this.userRepository.save(user));
    }

    @Override
    public List<OwnUserDTO> getOwners(String[] userIds) {
        var userUUID = Arrays.stream(userIds).map(UUID::fromString).collect(Collectors.toList());
        var users = this.userRepository.searchUser(userUUID);
        return userMapper.mapToListOwnUserDTO(users);
    }

    @Override
    public List<UserDTO> searchUser(Payload payload) {
        UserSpecification spec = new UserSpecification(payload);
        Pageable pageable = UserSpecification.getPageable(payload.getPage(), payload.getSize());
        var users = userRepository.findAll(spec, pageable).getContent();
        return userMapper.mapListUserToUserDTO(users);
    }
}


