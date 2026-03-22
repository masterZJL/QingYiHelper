package com.qingyi.helper.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新 Token 请求
 */
@Data
public class RefreshTokenRequest {

    @NotBlank(message = "refreshToken不能为空")
    private String refreshToken;
}
