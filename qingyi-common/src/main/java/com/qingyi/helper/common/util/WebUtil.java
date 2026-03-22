package com.qingyi.helper.common.util;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Web 工具类
 */
public class WebUtil {

    private WebUtil() {}

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    public static String getClientIp() {
        HttpServletRequest request = getRequest();
        if (request == null) return "unknown";
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多次代理取第一个
        if (StrUtil.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    public static String getToken() {
        HttpServletRequest request = getRequest();
        if (request == null) return null;
        String token = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public static Long getTenantId() {
        HttpServletRequest request = getRequest();
        if (request == null) return null;
        String tenantId = request.getHeader("X-Tenant-Id");
        return StrUtil.isNotBlank(tenantId) ? Long.parseLong(tenantId) : null;
    }
}
