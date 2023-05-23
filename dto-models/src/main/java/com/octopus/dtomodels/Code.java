package com.octopus.dtomodels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Code implements Serializable {

    public enum VerificationType {

        LOGIN("login"),
        SIGN_UP("sign_up"),

        FORGOT_PASSWORD("forgot_password"),
        ADD_MEMBER_WORKSPACE("add_member_workspace"),
        ADD_MEMBER_PROJECT("add_member_project");
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
