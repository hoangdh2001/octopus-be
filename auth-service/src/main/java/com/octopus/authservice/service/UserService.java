package com.octopus.authservice.service;


import com.octopus.authservice.dto.request.ChangePasswordRequest;
import com.octopus.authservice.dto.request.SignupRequest;
import com.octopus.authservice.model.User;
import com.octopus.dtomodels.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    User findUserById(String id);

    User updateUserByID(String id, UserDTO userDTO);
    Boolean existByEmailAndEnableIsTrue(String email);

    User findUserByEmail(String email);

    User createUserTemp(String email);

    Boolean existByEmail(String email);

    User createUser(SignupRequest signupRequest);

    User changePassword(String id, ChangePasswordRequest changePasswordRequest);

    User resetPassword(String email, String password);
}

