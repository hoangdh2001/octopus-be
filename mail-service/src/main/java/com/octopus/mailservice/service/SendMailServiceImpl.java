package com.octopus.mailservice.service;

import com.octopus.mailservice.email.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class SendMailServiceImpl implements SendMailService{
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private UserService userService;

    @Override
    public void sendActivationEmail(String email, String func) throws MessagingException {
        JavaMailSenderImpl mailSender = Utility.prepareMailSender();

        Locale locale = LocaleContextHolder.getLocale();

        Context ctx = new Context(locale);

        String verifyURL = "http://localhost/Nhom40KLTN/api/users/verify/" + userService.getUserByEmail(email).getVerificationCode();
        ctx.setVariable("url", verifyURL);


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new  MimeMessageHelper(message, false, "utf-8");

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
}
