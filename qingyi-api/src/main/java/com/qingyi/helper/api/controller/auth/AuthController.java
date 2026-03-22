package com.qingyi.helper.api.controller.auth;

import com.qingyi.helper.api.dto.auth.LoginRequest;
import com.qingyi.helper.api.dto.auth.LoginResponse;
import com.qingyi.helper.api.dto.auth.RefreshTokenRequest;
import com.qingyi.helper.auth.model.LoginUser;
import com.qingyi.helper.auth.model.TokenInfo;
import com.qingyi.helper.auth.service.TokenService;
import com.qingyi.helper.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证 Controller
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // TODO: Phase 1 后续对接数据库查询用户
        // 当前使用硬编码的默认管理员账号做演示
        // 后续实现 SysUserService 从数据库查询

        // 模拟用户验证（后续替换为数据库查询）
        if (!"admin".equals(request.getUsername())) {
            return R.failed(401, "用户名或密码错误");
        }

        // 验证密码（默认 admin/admin123）
        if (!passwordEncoder.matches(request.getPassword(), "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH")) {
            return R.failed(401, "用户名或密码错误");
        }

        LoginUser loginUser = LoginUser.builder()
                .userId(1L)
                .tenantId(1L)
                .username("admin")
                .nickname("管理员")
                .role("ADMIN")
                .build();

        TokenInfo tokenInfo = tokenService.generateTokenPair(loginUser);

        LoginResponse response = LoginResponse.builder()
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .expiresIn(tokenInfo.getExpiresIn())
                .userInfo(LoginResponse.UserInfo.builder()
                        .userId(loginUser.getUserId())
                        .username(loginUser.getUsername())
                        .nickname(loginUser.getNickname())
                        .role(loginUser.getRole())
                        .build())
                .build();

        return R.ok(response);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public R<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenInfo tokenInfo = tokenService.refreshToken(request.getRefreshToken());

        return R.ok(LoginResponse.builder()
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .expiresIn(tokenInfo.getExpiresIn())
                .build());
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        // TODO: 从 SecurityContext 获取当前用户 Token 并加入黑名单
        return R.ok();
    }
}
