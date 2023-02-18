package com.octopus.authservice.service;

import com.octopus.authservice.exception.UserNotFoundException;
import com.octopus.authservice.model.Role;
import com.octopus.authservice.model.User;
import com.octopus.authservice.repository.RoleRepository;
import com.octopus.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    public static final int USER_PER_PAGE = 4;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    public PasswordEncoder passwordEncoder;


    @Override
    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public List<User> listAll() {
        return (List<User>) userRepository.findAll();
    }

    public Page<User> listByPage(int pageNum, String sortField, String sortDir , String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);

        if(keyword != null) {
            return userRepository.findAll(keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }

    public List<Role> listRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    public User save(User user) {
        boolean isUpdatingUser = (user.getId() > 0);

        if (isUpdatingUser) {
            User existingUser = userRepository.findById(user.getId()).get();
            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }
        } else {
            encodePassword(user);
        }
        return userRepository.save(user);
    }

    public User updateAccount(User userInForm) {
        User userInDB = userRepository.findById(userInForm.getId()).get();
        if(userInDB.getPassword().isEmpty()) {
            userInDB.setPassword(userInForm.getPassword());
            encodePassword(userInDB);
        }

        if(userInDB.getPhotos() != null) {
            userInDB.setPhotos(userInForm.getPhotos());
        }

        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());

        return userRepository.save(userInDB);
    }

    private void encodePassword(User user) {
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepository.getUserByEmail(email);
        if (userByEmail == null)
            return true;

        boolean isCreatingNew = (id == null);

        if (isCreatingNew) {
            if (userByEmail != null)
                return false;
        } else {
            if (userByEmail.getId() != id) {
                return false;
            }
        }

        return true;
    }

    public User getUserById(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (Exception e) {
            throw new UserNotFoundException("Could not find any user with ID" + id);
        }
    }

    public void deleteUserById(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find any user with ID" + id);
        }

        userRepository.deleteById(id);
    }

    public void updateUserEnabledStatus(Integer id, boolean endabled) {
        userRepository.updateEnableStatus(id, endabled);
    }

    public String getEmailOfAuthenticatedUser(HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        String customerEmail = null;

        if(principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken){
            customerEmail = request.getUserPrincipal().getName();
        }

        return customerEmail;
    }

    @Override
    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);
        if(user != null && !user.isEnable()) {
            userRepository.enabled(user.getId());
            return true;
        }else {
            return false;
        }
    }

    public long getCount() {
        return userRepository.count();
    }

    @Override
    public void updatePassword(String token, String newPassword) throws UserNotFoundException {
        User user = userRepository.findByResetPasswordToken(token);
        if (user == null) {
            throw new UserNotFoundException("Invalid Token");
        }
        user.setPassword(newPassword);
        encodePassword(user);
        userRepository.save(user);
    }

    @Override
    public User getByRestPasswordToken(String token) {
        System.out.println(userRepository.findByResetPasswordToken(token));
        return userRepository.findByResetPasswordToken(token);
    }

    @Override
    public String updateResetPasswordToken(String email) throws UserNotFoundException {
        User user = userRepository.getUserByEmail(email);
        if(user != null) {
            String token = RandomString.make(30);
            user.setResetPasswordToken(token);
            userRepository.save(user);

            return token;
        }else {
            throw new UserNotFoundException("cound not find customer with email "+ email);
        }
    }

    @Override
    public User findByEmail(String email) {
        return null;
    }


}


