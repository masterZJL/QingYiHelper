package com.qingyi.helper.auth.filter;

import com.qingyi.helper.auth.model.LoginUser;
import com.qingyi.helper.auth.service.TokenService;
import com.qingyi.helper.common.util.WebUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT 认证过滤器
 * 从请求头中提取 Token，解析用户信息，注入 SecurityContext
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 不需要认证的路径
     */
    private static final List<String> WHITE_LIST = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/doc.html",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/favicon.ico",
            "/actuator/**"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return WHITE_LIST.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = WebUtil.getToken();

        if (StringUtils.hasText(token) && tokenService.validateToken(token)) {
            try {
                LoginUser loginUser = tokenService.parseToken(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                loginUser,
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_" + loginUser.getRole())
                                )
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("用户[{}]认证成功, tenantId={}", loginUser.getUsername(), loginUser.getTenantId());
            } catch (Exception e) {
                log.warn("Token解析失败: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
