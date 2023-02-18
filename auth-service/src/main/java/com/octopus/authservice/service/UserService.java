package com.octopus.authservice.service;


import com.octopus.authservice.exception.UserNotFoundException;
import com.octopus.authservice.model.Role;
import com.octopus.authservice.model.User;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface UserService {
    public static final int USER_PER_PAGE = 4;
    Optional<User> findUserById(int id);
    public User getUserByEmail(String email);
    List<User> listAll();
    public Page<User> listByPage(int pageNum, String sortField, String sortDir , String keyword);
    public List<Role> listRoles();
    public User save(User user);
    public User updateAccount(User userInForm);

    private void encodePassword(User user) {

    }

    public boolean isEmailUnique(Integer id, String email);
    public User getUserById(Integer id) throws UserNotFoundException;
    public void deleteUserById(Integer id) throws UserNotFoundException;
    public void updateUserEnabledStatus(Integer id, boolean endabled);
    public String getEmailOfAuthenticatedUser(HttpServletRequest request);
    public boolean verify(String verificationCode);
    public long getCount();
    public void updatePassword(String token, String newPassword) throws UserNotFoundException;
    public User getByRestPasswordToken(String token);
    public String updateResetPasswordToken(String email) throws UserNotFoundException;
    public User findByEmail(String email);


}

