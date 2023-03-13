package com.octopus.authservice.controller;

import ch.qos.logback.classic.pattern.EnsureExceptionHandling;
import com.octopus.authservice.dto.request.LoginRequest;
import com.octopus.authservice.dto.request.UserRequest;
import com.octopus.authservice.dto.response.LoginResponse;
import com.octopus.authservice.dto.response.UserResponse;
import com.octopus.authservice.email.Utility;
import com.octopus.authservice.kafka.AuthProducer;
import com.octopus.authservice.model.User;
import com.octopus.authservice.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@CrossOrigin
//@Retry(name = "service-java")
//@CircuitBreaker(name = "service-java")
//@RateLimiter(name = "service-java")
public class UserController {
    private final UserService userService;

    private final TemplateEngine templateEngine;

    public final PasswordEncoder passwordEncoder;

    public final AuthProducer authProducer;

    @PostMapping(value = "/login")
    //@Retry(name = "service-java")
    @Operation(summary = "login for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "login successfully!"),
            @ApiResponse(responseCode = "403", description = "incorrect username or password")
    })
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest loginRequest) {
        String password = loginRequest.getPassword();
        User user = userService.getUserByEmail(loginRequest.getEmail());
        loginRequest.setUserID(user.getId());
        loginRequest.setOtp(user.getRefreshToken());
        LoginResponse loginResponse = this.userService.login(loginRequest.getEmail(), password);
        authProducer.sendMessage(loginRequest);
        return ResponseEntity.accepted().body(loginResponse.getToken());
    }

    @PostMapping(value = "/login_not_password")
    //@Retry(name = "service-java")
    @Operation(summary = "login for user but don't use password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "login successfully!"),
            @ApiResponse(responseCode = "405", description = "incorrect username")
    })
    public ResponseEntity<String> loginNotPassword(@RequestBody LoginRequest userRequest) {
        System.out.println(userRequest.getEmail());
        User user = this.userService.findByEmail(userRequest.getEmail());
        System.out.println(user);
        String password = userService.getUserByEmail(userRequest.getEmail()).getPassword();
        System.out.println(password);
        user.setVerificationCode(RandomStringUtils.randomAlphanumeric(30));
        String radomOTP = RandomStringUtils.randomNumeric(4);
        user.setRefreshToken(radomOTP+user.getId());
        userRequest.setOtp(radomOTP+user.getId());
        userRequest.setUserID(user.getId());
        /*try {
            sendVerificationEmail(userRequest.getEmail(), "login");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }*/
        if(userService.verify(userService.getUserByEmail(userRequest.getEmail()).getVerificationCode()) != null) {
            LoginResponse loginResponse = this.userService.loginNotPassword(userRequest.getEmail());
            System.out.println(passwordEncoder.encode(password));
            authProducer.sendMessage(userRequest);
            return ResponseEntity.accepted().body(loginResponse.getToken());
        }
        return ResponseEntity.badRequest().body("error");
    }

    @Operation(summary = "register for new user")
    @PostMapping("/register")
    //@Retry(name = "service-java")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create New User successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request!")
    })
    public ResponseEntity<Object> register(@Valid @RequestBody UserRequest userRequest) {
        userRequest.setActive(false);
        userRequest.setVerificationCode(RandomStringUtils.randomAlphanumeric(30));
        UserResponse userResponse = this.userService.register(userRequest);

        try {
            sendVerificationEmail(userRequest.getEmail(), "register");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(userResponse);
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

    @PostMapping("/checkmails")
    public boolean checkMail(@RequestBody String mail){
        User user = userService.getUserByEmail(mail);
        System.out.println(user.getEmail());
        if(user != null){
            return true;
        }
        return false;
    }

    @GetMapping("/{id}")
    @Operation(summary = "find user by id")
    public ResponseEntity<Object> findUser(@PathVariable int id) {
        System.out.println(id);
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
        user.setVerificationCode(RandomStringUtils.randomAlphanumeric(30));
        return ResponseEntity.ok().body(userService.createUser(user) != null);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update user")
    public ResponseEntity<Boolean> update(@RequestBody UserRequest user, @PathVariable int id) {
        user.setUpdatedDate(new Date());
        return ResponseEntity.ok().body(userService.updateUser(user, id) != null);
    }

    @DeleteMapping("/{id}")
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
    public User verifyAccount(
            @PathVariable("code") String verificationCode,
            RedirectAttributes redirectAttributes, Model model) {
        User verified = userService.verify(verificationCode);
        userService.loginNotPassword(verified.getEmail());
        redirectAttributes.addFlashAttribute("message", "verificationCode");
        return verified;
    }

    @GetMapping("/otp/{code}")
    public User checkOTP(@PathVariable("code") String otp, RedirectAttributes redirectAttributes, Model model) {
        User user = userService.checkOTP(otp);
        redirectAttributes.addFlashAttribute("message", "otp");
        return user;
    }

    private void sendVerificationEmail(@RequestBody String email, String func) throws UnsupportedEncodingException, MessagingException {
        JavaMailSenderImpl mailSender = Utility.prepareMailSender();

        Locale locale = LocaleContextHolder.getLocale();

        Context ctx = new Context(locale);

        String verifyURL = "http://localhost:8088/Nhom40KLTN/api/users/verify/" + userService.getUserByEmail(email).getVerificationCode();
        ctx.setVariable("url", verifyURL);


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

        //userService.findByEmail(user.getEmail());
        //String toAddress = user.getEmail();
        String toAddress = email;
        System.out.println(toAddress);
        helper.setTo(toAddress);
        helper.setSubject("Hỗ trợ octopus Nhóm 40 Khóa luận tốt nghiệp(HK2 - 2022)");

        String htmlContent = "";
        String s="";
        if(func.equalsIgnoreCase("login")){
            s = "mails/email_loginwithpassword.html";
        } else if(func.equalsIgnoreCase("register")){
            s = "mails/email_registered.html";
        }
        htmlContent = templateEngine.process(s, ctx);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @PostMapping("/info")
    public String editInfoUser(HttpServletRequest request, Model model, @Param("id") Integer id, User user) {

        return null;
    }



}
