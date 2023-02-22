package com.octopus.mailservice.email;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

public class Utility {
    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURI().toString();

        return siteURL.replace(request.getServletPath(), "");
    }

    public static JavaMailSenderImpl prepareMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("Nhom40KLTN@gmail.com");
        mailSender.setPassword("hlrhcpofyoimwpaq");
        //mailSender.setPassword("@Nhom40KLTN123");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.mime.charset", "utf-8");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
