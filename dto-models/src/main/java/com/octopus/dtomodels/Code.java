package com.octopus.dtomodels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Code {

    public enum VerificationType {

        LOGIN("login"),
        SIGN_UP("sign_up"),

        FORGOT_PASSWORD("forgot_password");
        private final String type;

        VerificationType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public static VerificationType rawValue(String value) {
            for (VerificationType e : values()) {
                if (e.type.equals(value)) {
                    return e;
                }
            }
            return null;
        }
    }

    private Integer otp;
    private String verificationCode;
    private String email;
    private VerificationType verificationType;
}
