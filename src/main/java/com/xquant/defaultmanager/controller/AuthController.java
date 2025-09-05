package com.xquant.defaultmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 登录认证
 * 处理用户登录认证相关请求
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // 认证管理器，用于处理用户名密码认证
    private final AuthenticationManager authenticationManager;

    /**
     * 构造函数，注入认证管理器
     * @param authenticationManager Spring Security认证管理器
     */
    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * 用户登录接口
     * 使用Spring Security进行认证，基于Session维持登录状态
     * @param request 登录请求参数，包含用户名和密码
     * @param httpRequest HTTP请求对象，用于创建会话
     * @return 登录结果，包含成功状态和用户名
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        // 创建认证令牌
        UsernamePasswordAuthenticationToken token = 
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        // 执行认证
        Authentication authentication = authenticationManager.authenticate(token);
        // 设置认证信息到安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 确保创建会话（基于 Session 维持登录）
        httpRequest.getSession(true);
        // 返回登录成功响应
        return ResponseEntity.ok(Map.of(
                "success", true,
                "username", request.getUsername()
        ));
    }

    /**
     * 登录请求参数内部类
     * 用于接收登录请求中的用户名和密码
     */
    public static class LoginRequest {
        private String username; // 用户名
        private String password; // 密码

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
