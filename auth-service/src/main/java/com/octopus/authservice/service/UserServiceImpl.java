package com.octopus.authservice.service;

import com.octopus.authservice.dto.request.UserRequest;
import com.octopus.authservice.dto.response.LoginResponse;
import com.octopus.authservice.dto.response.UserResponse;
import com.octopus.authservice.exception.ForbiddenException;
import com.octopus.authservice.exception.ResourceNotFoundException;
import com.octopus.authservice.exception.UserNotFoundException;
import com.octopus.authservice.mapping.MapData;
import com.octopus.authservice.model.User;
import com.octopus.authservice.repository.UserRepository;
import com.octopus.authservice.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    public static final int USER_PER_PAGE = 4;
    private final UserRepository userRepository;

    public final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

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
            return userRepository.findByKeyword(keyword, pageable);
        }
        return userRepository.findAll(pageable);
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

        if(userInDB.getAvatar() != null) {
            userInDB.setAvatar(userInForm.getAvatar());
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
    public User verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);
        System.out.println(user.isActive() +"......." + user.getId());
        /*if(user != null && !user.isActive()) {
            userRepository.enabled(user.getId());
            return true;
        }else {
            return false;
        }*/
        return user;
    }

    public long getCount() {
        return userRepository.count();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AuthenticationManager userManager, JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = userManager;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            System.out.println(role);
            String token = jwtProvider.createToken(username, String.valueOf(role));
            return new LoginResponse(role, token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new ForbiddenException("username or password is incorrect!");
        }
    }

    @Override
    public LoginResponse loginNotPassword(String username) {
        try {
            //Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, null));
            String role = "user";//authentication.getAuthorities().iterator().next().getAuthority();
            System.out.println(role);
            String token = jwtProvider.createToken(username, String.valueOf(role));
            return new LoginResponse(role, token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new ForbiddenException("username or password is incorrect!");
        }
    }


    @Override
    public UserResponse register(UserRequest userRequest) {
        User user = MapData.mapOne(userRequest, User.class);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        //user.setCreateTime(SimpleDateFormat.getDateInstance().format(new Date()));
        //user.setRole(Role.valueOf(userRequest.getRole()));
        if(userRepository.getUserByEmail(user.getEmail()) == null){
            User accountSaved = this.userRepository.save(user);
            UserResponse userResponse = MapData.mapOne(accountSaved, UserResponse.class);
            return userResponse;
        }
        //User user = new User();
        //user.setAccount(accountSaved);
        //this.userRepository.save(user);

        //userResponse.setUser(MapData.mapOne(user, UserResponse.class));
        return null;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public UserResponse findUserById(int id) {
        // TODO Auto-generated method stub
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("not found user"));
        return MapData.mapOne(user, UserResponse.class);
    }

    @Override
    @Cacheable(value = "users")
    public List<UserResponse> findAllUser() {
        // TODO Auto-generated method stub
        return MapData.mapList(this.userRepository.findAllUser(), UserResponse.class);
    }

    @Override
    @Cacheable(value = "user")
    public UserResponse createUser(UserRequest user) {
        // TODO Auto-generated method stub
        User newUser = MapData.mapOne(user, User.class);
        User userSaved = this.userRepository.save(newUser);
        return MapData.mapOne(userSaved, UserResponse.class);
    }

    @Override
    @CachePut(value = "user", key = "#id")
    public UserResponse updateUser(UserRequest userRequest, int id) {
        // TODO Auto-generated method stub
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("not found user"));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        return MapData.mapOne(this.userRepository.save(user), UserResponse.class);
    }

    @Override
    @CacheEvict(value = "user", key = "#id")
    public void delete(int id) {
        // TODO Auto-generated method stub
        this.userRepository.delete(this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("not found user")));
    }

    @Override
    @Cacheable(value = "user")
    public UserResponse ownProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        return MapData.mapOne(user, UserResponse.class);
    }

    @Override
    @CachePut(value = "user")
    public UserResponse updateOwnProfile(UserRequest userRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.getUserByEmail(username);
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        System.out.println(userRequest.getEmail());
        System.out.println(userRequest.getFirstName()+" "+userRequest.getLastName());
        return MapData.mapOne(this.userRepository.save(user), UserResponse.class);
    }

    @Override
    public User checkOTP(String otp) {
        User user = userRepository.findByOTP(otp);
        return user;
    }


}


