package com.octopus.authservice.controller;

import com.octopus.authservice.email.Utility;
import com.octopus.authservice.exception.UserNotFoundException;
import com.octopus.authservice.model.Role;
import com.octopus.authservice.model.User;
import com.octopus.authservice.repository.RoleRepository;
import com.octopus.authservice.service.UserService;
import com.octopus.authservice.upload.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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

    /*@GetMapping("/")
    public String loadLoginPage() {
        return "login";
    }*/

    @PostMapping("/")
    public User addUser(@RequestBody User user){
        User newUser = new User();
        userService.save(newUser);
        if(newUser != null){
            return newUser;
        }
        return null;
    }

    @PostMapping("/login")
    public User Login(@RequestBody User user) throws MessagingException, UnsupportedEncodingException {
        User oldUSer = userService.findByEmail(user.getEmail());
        if(oldUSer != null){
            sendVerificationEmail(oldUSer);
            return oldUSer;
        }
        return null;
    }

    /*@GetMapping("/login")
    public String viewsLoginPage() {
        return "login";
    }*/

    @GetMapping("/users")
    public String listFirstPage(
            HttpServletRequest request,
            Model model) {
        return listByPage(request,1, "id", "asc", null ,model);
    }
    @GetMapping("/users/page/{pageNum}")
    public String listByPage(
            HttpServletRequest request,
            @PathVariable(name = "pageNum") int pageNum,
            @Param("sortField") String sortField,
            @Param("sortDir") String sortDir,
            @Param("keyword") String keyword,
            Model model) {
        String email = userService.getEmailOfAuthenticatedUser(request);
        User user = userService.getUserByEmail(email);

        Page<User> page = userService.listByPage(pageNum, sortField, sortDir, keyword);
        List<User> listUsers = page.getContent();

        long startCount = (pageNum - 1) * UserService.USER_PER_PAGE + 1;
        long endCount = startCount + UserService.USER_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String sortOrther = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("user", user);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItem", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrther", sortOrther);
        model.addAttribute("keyword", keyword);

        return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(
            Model model) {
        List<Role> listRoles = userService.listRoles();
        User user = new User();
        user.setEnable(true);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", " Created New User");
        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(
            User user,
            RedirectAttributes redirectAttributes,
            @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()).replace(" ", "");
            user.setPhotos(fileName.trim());
            User savadUser = userService.save(user);
            String uploadDir = "user-photos/" + savadUser.getId();
            FileUploadUtil.clearDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty())
                user.setPhotos(null);
            userService.save(user);
        }
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");

        return getRedirectURLtoUser(user);
    }

    private String getRedirectURLtoUser(User user) {
        String firstParOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstParOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(
            @PathVariable(name = "id") Integer id,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            User user = userService.getUserById(id);
            List<Role> listRoles = userService.listRoles();

            model.addAttribute("user", user);
            model.addAttribute("pageTitle", " Edit User (ID: " + id + ")");
            model.addAttribute("listRoles", listRoles);
            return "users/user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(
            @PathVariable(name = "id") Integer id,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("message", "The User ID" + id + " has been deleted successfully");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/user/{id}/enabled/{status}")
    public String updateEnabledStatus(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "status") boolean status,
            RedirectAttributes redirectAttributes) {
        userService.updateUserEnabledStatus(id, status);
        String enabled = status ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "The User ID" + id + " has been " + enabled + "");
        return "redirect:/users";
    }

    @GetMapping("/signup")
    public String viewsSignUpPage(Model model ) {
        User user  = new User();
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Sign Up");
        return "signup";
    }

    @PostMapping("/register")
    public User signUp(@RequestBody User user, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException, MessagingException {
        User userSave = userService.save(user);
        if(userSave != null) {
            sendVerificationEmail(user);
            return userSave;
        }
        return null;
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
