package com.qingyi.helper.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 登录用户信息（存入 SecurityContext）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    private Long userId;
    private Long tenantId;
    private String username;
    private String nickname;
    private String role;
    private Set<String> permissions;
}
