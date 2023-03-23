package com.octopus.mailservice.service;

import com.octopus.dtomodels.Code;

import javax.mail.MessagingException;


public interface SendMailService {
    void sendActivationEmail(Code code) throws MessagingException;

    void sendResetPasswordEmail(Code code) throws MessagingException;
}
