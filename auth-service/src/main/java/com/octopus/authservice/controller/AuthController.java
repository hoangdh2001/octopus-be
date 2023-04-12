package com.octopus.authservice.controller;

import com.octopus.authservice.dto.request.LoginRequest;
import com.octopus.authservice.dto.request.ResetRequest;
import com.octopus.authservice.dto.request.SignupRequest;
import com.octopus.authservice.dto.request.VerifyRequest;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Tag(name = "Authorization", description = "Login endpoint")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final KafkaProducer kafkaProducer;

    private final OTPService otpService;

    private final VerificationCodeService verificationCodeService;

    private final JWTUtils jwtUtils;

    private final UserMapper mapper;

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
                    User user = this.userService.findUserByEmail(loginRequest.getEmail());
                    return ResponseEntity.ok().body(createJWT(user));
                }
                throw new AuthenticationException("Wrong code or expire code");
            case LOGIN_WITH_CODE:
                if (loginRequest.getCode() == null) {
                    throw new IllegalArgumentException("Is " + loginType.getType() + " but code value is null");
                }
                var isValidCode = verificationCodeService.validateVerificationCode(loginRequest.getEmail(), loginRequest.getCode());
                if (isValidCode) {
                    User user = this.userService.findUserByEmail(loginRequest.getEmail());
                    return ResponseEntity.ok().body(createJWT(user));
                }
                throw new AuthenticationException("Wrong code or expire code");
            case LOGIN_WITH_PASSWORD:
                try {
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
                    User user = this.userService.findUserByEmail(loginRequest.getEmail());
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
        User user = this.userService.createUser(signupRequest);
        return ResponseEntity.ok().body(mapper.mapToUserDTO(user));
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
            User user = this.userService.resetPassword(resetRequest.getEmail(), resetRequest.getPassword());
            return ResponseEntity.ok().body(mapper.mapToUserDTO(user));
        }
        throw new InvalidDataException("Reset code not match or expire code");
    }

    private TokenResponse createJWT(User user) {
        var token = jwtUtils.generateToken(mapper.mapToUserDTO(user));
        var exp = jwtUtils.extractExpiration(token);
        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiredIn(exp.getTime())
                .user(mapper.mapToUserDTO(user))
                .verificationType(Code.VerificationType.LOGIN)
                .build();
    }
}
