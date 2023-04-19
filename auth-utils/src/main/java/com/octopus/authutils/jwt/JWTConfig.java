package com.octopus.authutils.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class JWTConfig {

    @Value("${security.jwt.uri:/api/auth}")
    private String authEndpoint = "/api/auth";

    @Value("${security.jwt.header:Authorization}")
    private String header = "Authorization";

    @Value("${security.jwt.prefix:Bearer }")
    private String tokenPrefix = "Bearer ";

    @Value("${security.jwt.expiration:#{24*60*60*30}}")
    private int expiration = 24*60*60;

    @Value("${security.jwt.secret:khoa_luan_tot_nghiep_nhom40_octopus}")
    private String secret = "khoa_luan_tot_nghiep_nhom40_octopus";

}
