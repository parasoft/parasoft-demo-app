package com.parasoft.demoapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.parasoft.demoapp.model.global.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema
public class ForgotPasswordResponseDTO {
    private String roleName;
    private boolean hasPrimaryUser;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PrimaryUserInfo primaryUserInfo;

    public ForgotPasswordResponseDTO(String roleName) {
        this.roleName = roleName;
        this.hasPrimaryUser = false;
    }

    public ForgotPasswordResponseDTO(String roleName, String userName, String password) {
        this.roleName = roleName;
        this.hasPrimaryUser = true;
        this.primaryUserInfo = new PrimaryUserInfo(userName, password);
    }

    public static ForgotPasswordResponseDTO getInstanceFrom(UserEntity user) {
        return new ForgotPasswordResponseDTO(user.getRole().getName(), user.getUsername(), user.getPassword());
    }

    public static ForgotPasswordResponseDTO getInstance(String roleName) {
        return new ForgotPasswordResponseDTO(roleName);
    }

    @Data
    public class PrimaryUserInfo {
        String userName;
        String password;

        public PrimaryUserInfo(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }
    }
}
