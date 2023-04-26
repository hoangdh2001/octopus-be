package com.octopus.authservice.service;

import com.octopus.authservice.dto.request.ChangePasswordRequest;
import com.octopus.authservice.dto.request.DeviceRequest;
import com.octopus.authservice.dto.request.SignupRequest;
import com.octopus.authservice.model.Device;
import com.octopus.authservice.model.User;
import com.octopus.authservice.repositories.DeviceRepository;
import com.octopus.authservice.repositories.UserRepository;
import com.octopus.exceptionutils.InvalidDataException;
import com.octopus.exceptionutils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final DeviceRepository deviceRepository;
    public final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public User findUserById(String id) {
        return this.userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Not found user with id " + id));
    }

    @Override
    public User updateUserByID(String id, User other) {
        var user = this.findUserById(id);
        user.merge(other);
        return this.userRepository.save(user);
    }

    @Override
    public Boolean existByEmailAndEnableIsTrue(String email) {
        return this.userRepository.existsByEmailIgnoreCaseAndEnabledIsTrue(email);
    }

    @Override
    @Cacheable(value = "user", key = "#email")
    public User findUserByEmail(String email) {
        return this.userRepository.findUserByEmailIgnoreCase(email).orElseThrow(() -> new NotFoundException("Not found user with email " + email));
    }

    @Override
    public User createUserTemp(String email) {
        var user = User.builder()
                .email(email)
                .build();
        return this.userRepository.save(user);
    }

    @Override
    public Boolean existByEmail(String email) {
        return this.userRepository.existsByEmailIgnoreCase(email);
    }

    public User createUser(SignupRequest signupRequest) {
        var user = this.findUserByEmail(signupRequest.getEmail());
        user.setEnabled(true);
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        return this.userRepository.save(user);
    }

    @Override
    public User changePassword(String id, ChangePasswordRequest changePasswordRequest) {
        var user = this.findUserById(id);
        var isValid = passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword());
        if (isValid) {
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPass()));
            return this.userRepository.save(user);
        }
        throw new InvalidDataException("Current password not match");
    }

    @Override
    public User resetPassword(String email, String password) {
        var user = this.findUserByEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }

    @Override
    public List<Device> findDevicesByUserId(String userID) {
        return this.deviceRepository.findDevicesByUserIdAndDisabledIsFalse(UUID.fromString(userID));
    }

    @Override
    public Device addDevice(DeviceRequest deviceRequest, String id) {
        var user = this.findUserById(id);
        var device = Device.builder()
                .pushProvider(deviceRequest.getPushProvider())
                .deviceID(deviceRequest.getDeviceID())
                .user(user)
                .build();
        return this.deviceRepository.save(device);
    }

    @Override
    public boolean removeDevice(String id) {
        return this.deviceRepository.disabledDevice(id);
    }


}


