package com.xquant.defaultmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // 允许公开访问的接口
                        .requestMatchers(
                                "/**",
                                "/counterparties/{id}",
                                "/counterparties/code/**",
                                "/counterparties",
                                "/counterparties/paged",
                                "/counterparties/rating/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()

//                        // 需要特定权限的接口
//                        .requestMatchers(
//                                "/counterparties/**"    // PUT/DELETE
//                        ).hasRole("ADMIN")

//                        // 其他所有请求需要认证
//                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()) // 禁用CSRF，适合API服务
                .httpBasic(httpBasic -> {});   // 启用HTTP Basic认证

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
