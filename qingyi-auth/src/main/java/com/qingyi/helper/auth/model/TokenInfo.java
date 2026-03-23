package com.qingyi.helper.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {

    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String tokenType;
}
