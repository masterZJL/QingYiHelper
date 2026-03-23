package com.qingyi.helper.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyi.helper.auth.filter.JwtAuthenticationFilter;
import com.qingyi.helper.common.R;
import com.qingyi.helper.common.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 * 面试话术：无状态 JWT 认证，关闭 Session，自定义认证过滤器
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 关闭 CSRF（前后端分离 + JWT 无状态）
            .csrf(AbstractHttpConfigurer::disable)
            // 关闭 Session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 授权规则
            .authorizeHttpRequests(auth -> auth
                    // 公开接口
                    .requestMatchers(
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
                    ).permitAll()
                    // 其他请求需要认证
                    .anyRequest().authenticated()
            )
            // 异常处理
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setCharacterEncoding("UTF-8");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write(
                                objectMapper.writeValueAsString(R.failed(ErrorCode.UNAUTHORIZED))
                        );
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setCharacterEncoding("UTF-8");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write(
                                objectMapper.writeValueAsString(R.failed(ErrorCode.FORBIDDEN))
                        );
                    })
            )
            // 替换默认的 UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
