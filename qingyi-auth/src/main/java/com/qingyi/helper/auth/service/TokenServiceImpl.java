package com.qingyi.helper.auth.service;

import com.qingyi.helper.auth.model.LoginUser;
import com.qingyi.helper.auth.model.TokenInfo;
import com.qingyi.helper.common.BizException;
import com.qingyi.helper.common.ErrorCode;
import com.qingyi.helper.common.util.JwtUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Token 服务实现
 * 面试话术：双 Token 机制，Access Token 短期有效（2小时），
 *          Refresh Token 长期有效（7天），登出时将 Token 加入 Redis 黑名单
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${qingyi.auth.access-token-expire:7200}")
    private long accessTokenExpire;

    @Value("${qingyi.auth.refresh-token-expire:604800}")
    private long refreshTokenExpire;

    @Value("${qingyi.auth.jwt-secret:qingyi-helper-default-jwt-secret-key-2024}")
    private String jwtSecret;

    private final StringRedisTemplate redisTemplate;

    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "token:refresh:";

    @Override
    public TokenInfo generateTokenPair(LoginUser loginUser) {
        long now = System.currentTimeMillis();

        // Access Token
        Map<String, Object> accessPayload = new HashMap<>();
        accessPayload.put("userId", loginUser.getUserId());
        accessPayload.put("tenantId", loginUser.getTenantId());
        accessPayload.put("username", loginUser.getUsername());
        accessPayload.put("role", loginUser.getRole());
        accessPayload.put("type", "access");
        accessPayload.put(JWT.ISSUED_AT, now / 1000);
        accessPayload.put(JWT.EXPIRES_AT, (now + accessTokenExpire * 1000) / 1000);

        String accessToken = JwtUtil.createToken(accessPayload, jwtSecret.getBytes());

        // Refresh Token
        Map<String, Object> refreshPayload = new HashMap<>();
        refreshPayload.put("userId", loginUser.getUserId());
        refreshPayload.put("type", "refresh");
        refreshPayload.put(JWT.ISSUED_AT, now / 1000);
        refreshPayload.put(JWT.EXPIRES_AT, (now + refreshTokenExpire * 1000) / 1000);

        String refreshToken = JwtUtil.createToken(refreshPayload, jwtSecret.getBytes());

        // 将 Refresh Token 存入 Redis，用于校验
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + loginUser.getUserId(),
                refreshToken,
                refreshTokenExpire,
                TimeUnit.SECONDS
        );

        return TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpire)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public LoginUser parseToken(String token) {
        try {
            JWT jwt = JwtUtil.getJwt(token);
            return LoginUser.builder()
                    .userId(Long.valueOf(jwt.getPayload("userId").toString()))
                    .tenantId(Long.valueOf(jwt.getPayload("tenantId").toString()))
                    .username(jwt.getPayload("username").toString())
                    .role(jwt.getPayload("role").toString())
                    .build();
        } catch (Exception e) {
            log.warn("解析Token失败: {}", e.getMessage());
            throw new BizException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public boolean validateToken(String token) {
        // 1. 检查黑名单
        Boolean inBlacklist = redisTemplate.hasKey(TOKEN_BLACKLIST_PREFIX + token);
        if (Boolean.TRUE.equals(inBlacklist)) {
            return false;
        }
        // 2. 验证签名和过期
        return JwtUtil.verify(token, jwtSecret.getBytes());
    }

    @Override
    public TokenInfo refreshToken(String refreshToken) {
        // 1. 验证 Refresh Token
        if (!JwtUtil.verify(refreshToken, jwtSecret.getBytes())) {
            throw new BizException(ErrorCode.TOKEN_INVALID);
        }

        JWT jwt = JwtUtil.getJwt(refreshToken);
        Long userId = Long.valueOf(jwt.getPayload("userId").toString());

        // 2. 校验 Redis 中存储的 Refresh Token 是否一致
        String storedRefreshToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new BizException(ErrorCode.TOKEN_INVALID);
        }

        // 3. 解析用户信息，生成新的 Token 对
        LoginUser loginUser = LoginUser.builder()
                .userId(userId)
                .tenantId(Long.valueOf(jwt.getPayload("tenantId").toString()))
                .username(jwt.getPayload("username").toString())
                .role(jwt.getPayload("role").toString())
                .build();

        return generateTokenPair(loginUser);
    }

    @Override
    public void revokeToken(String token) {
        // 将 Token 加入黑名单，过期时间与 Token 本身一致
        try {
            JWT jwt = JwtUtil.getJwt(token);
            Object exp = jwt.getPayload(JWT.EXPIRES_AT);
            long expireSeconds = 0;
            if (exp != null) {
                expireSeconds = Long.parseLong(exp.toString()) - System.currentTimeMillis() / 1000;
            }
            if (expireSeconds > 0) {
                redisTemplate.opsForValue().set(
                        TOKEN_BLACKLIST_PREFIX + token,
                        "1",
                        expireSeconds,
                        TimeUnit.SECONDS
                );
            }
        } catch (Exception e) {
            log.warn("撤销Token失败: {}", e.getMessage());
        }
    }
}
