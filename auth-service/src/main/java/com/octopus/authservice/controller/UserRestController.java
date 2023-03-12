package com.octopus.authservice.controller;

import com.octopus.authservice.model.User;
import com.octopus.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;

    @PostMapping("/check_email")
    public String checkDuplicateEmail(
            @Param("id") Integer id,
            @Param("email") String email) {
        return userService.isEmailUnique(id, email) ? "OK" :  "Duplicate";
    }

    @GetMapping("/get_email_authen")
    public Integer getEmailCustomerAuthentication(
            HttpServletRequest request) {
        String email = userService.getEmailOfAuthenticatedUser(request);
        User user = userService.getUserByEmail(email);
        if (user != null) {
            return user.getId();
        }
        return null;
    }

}
