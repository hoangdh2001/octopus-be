package com.octopus.authservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
public class RedirectController {
    @GetMapping
    public String redirect(
            @Param("type") String type,
            @Param("code") String code,
            @Param("email") String email
    ) {
        log.info(type + " " + code + " " + email);
        return "mails/redirect_to_deep_link";
    }
}
