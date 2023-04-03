package com.octopus.authservice.dto.request;

import com.octopus.authservice.valid.Password;
import com.octopus.dtomodels.Code;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    public enum LoginType {
        LOGIN_WITH_PASSWORD("login_with_password"),
        LOGIN_WITH_OTP("login_with_otp"),
        LOGIN_WITH_CODE("login_with_code");

        private final String type;

        LoginType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public static LoginType rawValue(String value) {
            for (LoginType e : values()) {
                if (e.type.equals(value)) {
                    return e;
                }
            }
            return null;
        }
    }

    @Email
    @NotBlank
    @NotNull
    private String email;
    private String password;
    private String code;
    @NotNull
    private String type;
    private Integer otp;
}
