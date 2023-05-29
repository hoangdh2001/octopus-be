package com.octopus.mailservice.service;

import com.octopus.dtomodels.Code;
import com.octopus.mailservice.email.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Configuration
@EnableAsync
public class SendMailServiceImpl implements SendMailService{

    @Value("${server.domain}")
    private String domain;
    private final TemplateEngine templateEngine;
    @Override
    public void sendActivationEmail(Code code) throws MessagingException {
        JavaMailSenderImpl mailSender = Utility.prepareMailSender();

        Locale locale = LocaleContextHolder.getLocale();

        Context ctx = new Context(locale);

        String verifyURL = String.format("http://%s?type=%s&code=%s&email=%s", domain, code.getVerificationType().getType(), code.getVerificationCode(), code.getEmail());
        ctx.setVariable("url", verifyURL);

        ctx.setVariable("otp", code.getOtp());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new  MimeMessageHelper(message, false, "utf-8");

        helper.setTo(code.getEmail());
        helper.setSubject(String.format("Sign into Octopus: %s", code.getOtp()));

        String s = "authmails/email_loginwithpassword.html";
        String htmlContent = templateEngine.process(s, ctx);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Override
    public void sendResetPasswordEmail(Code code) throws MessagingException {
        JavaMailSenderImpl mailSender = Utility.prepareMailSender();

        Locale locale = LocaleContextHolder.getLocale();

        Context ctx = new Context(locale);

        String verifyURL = String.format("http://%s/login/reset?type=%s&code=%s&email=%s", domain, code.getVerificationType().getType(), code.getVerificationCode(), code.getEmail());
        ctx.setVariable("url", verifyURL);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new  MimeMessageHelper(message, false, "utf-8");

        helper.setTo(code.getEmail());
        helper.setSubject("Octopus Team");

        String s = "authmails/email_forgotpassword.html";
        String htmlContent = templateEngine.process(s, ctx);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Override
    public void sendEmailAddMemberWorkspace(Code code) throws MessagingException {
        JavaMailSenderImpl mailSender = Utility.prepareMailSender();

        Locale locale = LocaleContextHolder.getLocale();

        Context ctx = new Context(locale);

        ctx.setVariable("name", code.getName());
        ctx.setVariable("workspace_name", code.getWorkspaceName());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new  MimeMessageHelper(message, false, "utf-8");

        helper.setTo(code.getEmail());
        helper.setSubject("Octopus Team");

        String s = "authmails/email_addmemberworkspace.html";
        String htmlContent = templateEngine.process(s, ctx);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Override
    public void sendEmailAddMemberProject(Code code) throws MessagingException {
        JavaMailSenderImpl mailSender = Utility.prepareMailSender();

        Locale locale = LocaleContextHolder.getLocale();

        Context ctx = new Context(locale);

        String verifyURL = String.format("http://%s/project/addmember?type=%s&code=%s&email=%s", domain, code.getVerificationType().getType(), code.getVerificationCode(), code.getEmail());
        ctx.setVariable("url", verifyURL);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new  MimeMessageHelper(message, false, "utf-8");

        helper.setTo(code.getEmail());
        helper.setSubject("Octopus Team");

        String s = "authmails/addmemberproject.html";
        String htmlContent = templateEngine.process(s, ctx);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
