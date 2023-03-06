package com.octopus.authservice.service;


import com.octopus.authservice.dto.request.UserRequest;
import com.octopus.authservice.dto.response.LoginResponse;
import com.octopus.authservice.dto.response.UserResponse;
import com.octopus.authservice.exception.UserNotFoundException;
import com.octopus.authservice.model.User;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService {
    public static final int USER_PER_PAGE = 4;
    public User getUserByEmail(String email);
    List<User> listAll();
    public Page<User> listByPage(int pageNum, String sortField, String sortDir , String keyword);
    public User save(User user);
    public User updateAccount(User userInForm);

    private void encodePassword(User user) {

    }

    public boolean isEmailUnique(Integer id, String email);
    public User getUserById(Integer id) throws UserNotFoundException;
    public void deleteUserById(Integer id) throws UserNotFoundException;
    public void updateUserEnabledStatus(Integer id, boolean endabled);
    public String getEmailOfAuthenticatedUser(HttpServletRequest request);
    public User verify(String verificationCode);
    public long getCount();
    //public void updatePassword(String token, String newPassword) throws UserNotFoundException;
    //public User getByRestPasswordToken(String token);
    //public String updateResetPasswordToken(String email) throws UserNotFoundException;
    public User findByEmail(String email);
    LoginResponse login(String username, String password);
    LoginResponse loginNotPassword(String username);
    UserResponse register(UserRequest accountRequest);

    void logout(HttpServletRequest request, HttpServletResponse response);
    UserResponse findUserById(int id);

    List<UserResponse> findAllUser();

    UserResponse createUser(UserRequest user);

    UserResponse updateUser(UserRequest userRequest, int id);

    void delete(int id);

    UserResponse ownProfile();

    UserResponse updateOwnProfile(UserRequest userRequest);

    User checkOTP(String otp);




}

