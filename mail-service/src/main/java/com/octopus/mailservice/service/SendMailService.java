package com.octopus.mailservice.service;

import javax.mail.MessagingException;


public interface SendMailService {
    void sendActivationEmail(String email, String func, String otp) throws MessagingException;
}
