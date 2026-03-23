package com.qingyi.helper.common.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

import java.util.Map;

/**
 * JWT 工具类
 */
public class JwtUtil {

    private JwtUtil() {}

    /**
     * 生成 JWT Token
     */
    public static String createToken(Map<String, Object> payload, byte[] key) {
        return JWTUtil.createToken(payload, key);
    }

    /**
     * 验证 Token
     */
    public static boolean verify(String token, byte[] key) {
        try {
            return JWTUtil.verify(token, key);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取 Token 中的载荷
     */
    public static JWT getJwt(String token) {
        return JWTUtil.parseToken(token);
    }

    /**
     * 判断 Token 是否过期
     */
    public static boolean isExpired(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            return jwt.validate(0);
        } catch (Exception e) {
            return true;
        }
    }
}
