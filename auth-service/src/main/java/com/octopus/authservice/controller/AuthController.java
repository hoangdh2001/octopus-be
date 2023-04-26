package com.octopus.authservice.controller;

import com.octopus.authservice.dto.request.*;
import com.octopus.authservice.dto.response.VerifyResponse;
import com.octopus.authservice.mapper.UserMapper;
import com.octopus.authservice.messaging.producer.KafkaProducer;
import com.octopus.authservice.model.User;
import com.octopus.authservice.security.models.TokenResponse;
import com.octopus.authservice.service.OTPService;
import com.octopus.authservice.service.UserService;
import com.octopus.authservice.service.VerificationCodeService;
import com.octopus.authutils.jwt.JWTUtils;
import com.octopus.dtomodels.Code;
import com.octopus.dtomodels.UserDTO;
import com.octopus.exceptionutils.AuthenticationException;
import com.octopus.exceptionutils.InvalidDataException;
import com.octopus.exceptionutils.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Tag(name = "Authorization", description = "Login endpoint")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Transactional
public class AuthController {

    private final UserService userService;

    private final KafkaProducer kafkaProducer;

    private final OTPService otpService;

    private final VerificationCodeService verificationCodeService;

    private final JWTUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Request verify email", description = "Send email")
    @PostMapping("/verify_email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Send email", content = @Content(array = @ArraySchema(schema = @Schema(implementation = VerifyResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<VerifyResponse> requestVerify(@Valid @RequestBody VerifyRequest verifyRequest) {
        var verificationType = Code.VerificationType.LOGIN;
        if (!this.userService.existByEmailAndEnableIsTrue(verifyRequest.getEmail())) {
            if (!this.userService.existByEmail(verifyRequest.getEmail())) {
                userService.createUserTemp(verifyRequest.getEmail());
            }
            verificationType = Code.VerificationType.SIGN_UP;
        }

        var code = verificationCodeService.generateVerificationCode(verifyRequest.getEmail());
        var otp = otpService.generateOTP(verifyRequest.getEmail());

        kafkaProducer.sendEmail(Code.builder()
                .otp(otp)
                .verificationCode(code)
                .email(verifyRequest.getEmail())
                .verificationType(verificationType)
                .build()
        );

        return ResponseEntity.
                ok()
                .body(VerifyResponse.builder()
                            .success(true)
                            .email(verifyRequest.getEmail())
                            .verificationType(verificationType)
                            .build()
        );
    }

    @Operation(summary = "Verify code", description = "Verify code")
    @PostMapping("/login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Send email", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TokenResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<TokenResponse> verifyCode(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        var loginType = LoginRequest.LoginType.rawValue(loginRequest.getType());
        if (loginType == null) {
            throw new IllegalArgumentException("Invalid argument field type");
        }
        switch (loginType) {
            case LOGIN_WITH_OTP:
                if (loginRequest.getOtp() == null) {
                    throw new IllegalArgumentException("Is " + loginType.getType() + " but otp value is null");
                }
                var isValidOTP = otpService.validateOTP(loginRequest.getEmail(), loginRequest.getOtp());
                if (isValidOTP) {
                    var user = this.userService.findUserByEmail(loginRequest.getEmail());
                    return ResponseEntity.ok().body(createJWT(user));
                }
                throw new AuthenticationException("Wrong code or expire code");
            case LOGIN_WITH_CODE:
                if (loginRequest.getCode() == null) {
                    throw new IllegalArgumentException("Is " + loginType.getType() + " but code value is null");
                }
                var isValidCode = verificationCodeService.validateVerificationCode(loginRequest.getEmail(), loginRequest.getCode());
                if (isValidCode) {
                    var user = this.userService.findUserByEmail(loginRequest.getEmail());
                    return ResponseEntity.ok().body(createJWT(user));
                }
                throw new AuthenticationException("Wrong code or expire code");
            case LOGIN_WITH_PASSWORD:
                try {
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
                    var user = this.userService.findUserByEmail(loginRequest.getEmail());
                    return ResponseEntity.ok().body(createJWT(user));
                } catch (Exception e) {
                    throw new AuthenticationException("Authentication failed");
                }
            default:
                throw new IllegalArgumentException("Invalid argument field type");
        }
    }


    @Operation(summary = "Sign up", description = "Sign up")
    @PostMapping("/signup")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Send email", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TokenResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody SignupRequest signupRequest) {
        var user = this.userService.createUser(signupRequest);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<VerifyResponse> forgotPassword(@Valid @RequestBody VerifyRequest verifyRequest) {
        if (this.userService.existByEmailAndEnableIsTrue(verifyRequest.getEmail())) {
            var code = verificationCodeService.generateVerificationCode(verifyRequest.getEmail());

            kafkaProducer.sendEmailForgotPassword(Code.builder()
                    .verificationCode(code)
                    .email(verifyRequest.getEmail())
                    .verificationType(Code.VerificationType.FORGOT_PASSWORD)
                    .build()
            );

            return ResponseEntity.ok().body(
                    VerifyResponse.builder()
                            .success(true)
                            .email(verifyRequest.getEmail())
                            .verificationType(Code.VerificationType.FORGOT_PASSWORD)
                            .build()
            );
        }
        throw new NotFoundException("Not found user with email " + verifyRequest.getEmail());
    }

    @PostMapping("/reset")
    public ResponseEntity<UserDTO> resetPassword(@Valid @RequestBody ResetRequest resetRequest) {
        var isValid = verificationCodeService.validateVerificationCode(resetRequest.getEmail(), resetRequest.getCode());
        if (isValid) {
            var user = this.userService.resetPassword(resetRequest.getEmail(), resetRequest.getPassword());
            return ResponseEntity.ok().body(user);
        }
        throw new InvalidDataException("Reset code not match or expire code");
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        var refreshToken = tokenRefreshRequest.getRefreshToken();
        if (jwtUtils.validateToken(refreshToken)) {
            var email = jwtUtils.extractUsername(refreshToken);
            var user = this.userService.findUserByEmail(email);
            return ResponseEntity.ok().body(createJWT(user));
        }
        throw new InvalidDataException("Refresh token invalid");
    }

    private TokenResponse createJWT(UserDTO user) {
        var token = jwtUtils.generateToken(user);
        var exp = jwtUtils.extractExpiration(token);
        var refreshToken = jwtUtils.generateRefreshToken(user);
        return TokenResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiredIn(exp.getTime())
                .user(user)
                .verificationType(Code.VerificationType.LOGIN)
                .build();
    }
}
