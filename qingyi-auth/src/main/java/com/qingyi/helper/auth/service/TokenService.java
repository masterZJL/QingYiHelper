package com.qingyi.helper.auth.service;

import com.qingyi.helper.auth.model.LoginUser;
import com.qingyi.helper.auth.model.TokenInfo;

/**
 * Token 服务接口
 */
public interface TokenService {

    /**
     * 生成 Token 对
     */
    TokenInfo generateTokenPair(LoginUser loginUser);

    /**
     * 从 Token 解析 LoginUser
     */
    LoginUser parseToken(String token);

    /**
     * 验证 Token 是否有效
     */
    boolean validateToken(String token);

    /**
     * 刷新 Token
     */
    TokenInfo refreshToken(String refreshToken);

    /**
     * 使 Token 失效（登出）
     */
    void revokeToken(String token);
}
