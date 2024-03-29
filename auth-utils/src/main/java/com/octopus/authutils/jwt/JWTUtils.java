package com.octopus.authutils.jwt;

import com.octopus.authutils.security.SecurityUserDetails;
import com.octopus.authutils.security.SecurityUserDetailsImpl;
import com.octopus.dtomodels.UserDTO;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class JWTUtils {

    private final JWTConfig jwtConfig;

    public JWTUtils(JWTConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        log.info(jwtConfig.getSecret());
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public String extractUserID(String token) {
        return extractClaims(token, Claims::getId);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token).getBody();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(SecurityUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), userDetails.getId());
    }

    public String generateToken(UserDTO userDTO) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDTO.getId());
        claims.put("email", userDTO.getEmail());
        if (userDTO.getFirstName() != null) {
            claims.put("firstName", userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            claims.put("lastName", userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null) {
            claims.put("phoneNumber", userDTO.getPhoneNumber());
        }
        if (userDTO.getBirthday() != null) {
            claims.put("birthday", userDTO.getBirthday());
        }
        if (userDTO.getGender() != null) {
            claims.put("gender", userDTO.getGender());
        }
        if (userDTO.getCreatedDate() != null) {
            claims.put("createdDate", userDTO.getCreatedDate());
        }
        if (userDTO.getUpdatedDate() != null) {
            claims.put("updatedDate", userDTO.getUpdatedDate());
        }
        return createToken(claims, userDTO.getEmail(), userDTO.getId());
    }

    public String generateRefreshToken(UserDTO userDTO) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDTO.getId());
        claims.put("email", userDTO.getEmail());
        if (userDTO.getFirstName() != null) {
            claims.put("firstName", userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            claims.put("lastName", userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null) {
            claims.put("phoneNumber", userDTO.getPhoneNumber());
        }
        if (userDTO.getBirthday() != null) {
            claims.put("birthday", userDTO.getBirthday());
        }
        if (userDTO.getGender() != null) {
            claims.put("gender", userDTO.getGender());
        }
        if (userDTO.getCreatedDate() != null) {
            claims.put("createdDate", userDTO.getCreatedDate());
        }
        if (userDTO.getUpdatedDate() != null) {
            claims.put("updatedDate", userDTO.getUpdatedDate());
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDTO.getEmail())
                .setId(userDTO.getId())
                .setExpiration(new Date(System.currentTimeMillis() + 315569520000L))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret()).compact();

    }

    private String createToken(Map<String, Object> claims, String subject, String id) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setId(id)
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration() * 1000L))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }

    public Boolean validateToken(String token, SecurityUserDetails userDetails) {
        final String userID = extractUsername(token);
        return (userID.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateToken(String authToken) {
        try {
            extractAllClaims(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException |
                 ExpiredJwtException ex) {
            throw ex;
        }
    }

    public boolean isValidAuthorizationHeaderValue(String authHeaderValue) {
        return StringUtils.isNotBlank(authHeaderValue)
                && authHeaderValue.contains(jwtConfig.getTokenPrefix())
                && StringUtils.isNotBlank(authHeaderValue.replace(jwtConfig.getTokenPrefix(), ""));
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        var userID = extractUserID(token);
        var username = extractUsername(token);
        var securityUser = new SecurityUserDetailsImpl(userID, username, "", new ArrayList<>());

        return new UsernamePasswordAuthenticationToken(securityUser, token, new ArrayList<>());
    }

    public JWTConfig getJwtConfig() {
        return jwtConfig;
    }
}
