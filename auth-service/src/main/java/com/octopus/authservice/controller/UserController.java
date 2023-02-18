package com.octopus.authservice.controller;

import com.octopus.authservice.dto.LoginRequestDTO;
import com.octopus.authservice.dto.LoginResponseDTO;
import com.octopus.authservice.dto.request.LoginRequest;
import com.octopus.authservice.dto.request.UserRequest;
import com.octopus.authservice.dto.response.LoginResponse;
import com.octopus.authservice.dto.response.UserResponse;
import com.octopus.authservice.email.Utility;
import com.octopus.authservice.exception.UserNotFoundException;
import com.octopus.authservice.model.Role;
import com.octopus.authservice.model.User;
import com.octopus.authservice.repository.RoleRepository;
import com.octopus.authservice.service.UserService;
import com.octopus.authservice.upload.FileUploadUtil;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;


@Controller
public class UserController {
    @Autowired
    private UserService userService;

    private TemplateEngine templateEngine;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/login")
    @Retry(name = "service-java")
    @Operation(summary = "login for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "login successfully!"),
            @ApiResponse(responseCode = "403", description = "incorrect username or password")
    })
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest loginRequest) {
        String password = loginRequest.getPassword();
        LoginResponse loginResponse = this.userService.login(loginRequest.getEmail(), password);
        return ResponseEntity.accepted().body(loginResponse.getToken());
    }

    @Operation(summary = "register for new user")
    @PostMapping("/register")
    @Retry(name = "service-java", fallbackMethod = "registerFallback")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create New User successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request!")
    })
    public ResponseEntity<Object> register(@Valid @RequestBody UserRequest accountRequest) {
        UserResponse accountResponse = this.userService.register(accountRequest);
        return ResponseEntity.ok().body(accountResponse);
    }

    @Operation(summary = "logout for user")
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.userService.logout(httpServletRequest, httpServletResponse);
        return ResponseEntity.ok("Logout.user.successfully!");
    }

    public ResponseEntity<String> fallBackLogin(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallback method called !Cannot login now");
    }

    public ResponseEntity<String> registerFallback(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallback method called !Cannot register now");
    }

    @GetMapping("{id}")
    @Operation(summary = "find user by id")
    public ResponseEntity<Object> findUser(@PathVariable int id) {
        return ResponseEntity.ok().body(userService.findUserById(id));
    }

    @GetMapping("/all")
    @Operation(summary = "get all user")
    public ResponseEntity<Object> findAll() {
        System.err.println("get all user");
        return ResponseEntity.ok().body(userService.listAll());
    }

    @PostMapping("/create")
    @Operation(summary = " create new user")
    public ResponseEntity<Boolean> save(@RequestBody UserRequest user) {
        return ResponseEntity.ok().body(userService.createUser(user) != null);
    }

    @PutMapping("{id}")
    @Operation(summary = "update user")
    public ResponseEntity<Boolean> update(@RequestBody UserRequest user, @PathVariable int id) {
        return ResponseEntity.ok().body(userService.updateUser(user, id) != null);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "delete user by id")
    public ResponseEntity<Boolean> delete(@PathVariable int id) {
        userService.delete(id);
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/me")
    @Operation(summary = "get current user")
    public ResponseEntity<Object> ownProfile() {
        return ResponseEntity.ok().body(userService.ownProfile());
    }

    @PutMapping("/me")
    @Operation(summary = "update current user")
    public ResponseEntity<Boolean> updateOwnProfile(@RequestBody UserRequest user) {
        System.err.println(user.getLastName());
        return ResponseEntity.ok().body(userService.updateOwnProfile(user) != null);
    }

    @GetMapping("/verify/{code}")
    public String verifyAccount(
            @PathVariable("code") String verificationCode,
            RedirectAttributes redirectAttributes, Model model) {
        boolean verified = userService.verify(verificationCode);
        redirectAttributes.addFlashAttribute("message", "verificationCode");
        return "redirect:/" + (verified ? "login" : "login?verify=flase");
    }

    private void sendVerificationEmail(@RequestBody User user) throws UnsupportedEncodingException, MessagingException {
        JavaMailSenderImpl mailSender = Utility.prepareMailSender();

        Locale locale = LocaleContextHolder.getLocale();

        Context ctx = new Context(locale);

        String verifyURL = "http://localhost/Nhom40KLTN/verify/" + user.getVerificationCode();
        ctx.setVariable("url", verifyURL);


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

        String toAddress = user.getEmail();
        helper.setTo(toAddress);
        helper.setSubject("Hỗ trợ octopus Nhóm 40 Khóa luận tốt nghiệp(HK2 - 2022)");

        String htmlContent = "";
        htmlContent = templateEngine.process("mails/email_registered.html", ctx);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @PostMapping("/user/info")
    public String editInfoUser(HttpServletRequest request, Model model, @Param("id") Integer id, User user) {

        return null;
    }



}
