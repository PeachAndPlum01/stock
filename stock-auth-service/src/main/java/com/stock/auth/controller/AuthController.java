package com.stock.auth.controller;

import com.stock.auth.entity.User;
import com.stock.auth.service.AuthService;
import com.stock.common.annotation.Idempotent;
import com.stock.common.annotation.OperationLog;
import com.stock.common.dto.LoginRequest;
import com.stock.common.dto.RegisterRequest;
import com.stock.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证API", description = "用户登录、注册、登出等认证相关接口")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名和密码登录系统，成功后返回JWT Token")
    @OperationLog(module = "认证模块", operation = "用户登录")
    public Result<Map<String, Object>> login(
            @Parameter(description = "登录请求参数", required = true)
            @Valid @RequestBody LoginRequest request) {
        Map<String, Object> result = authService.login(request.getUsername(), request.getPassword());
        return Result.success("登录成功", result);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账号，需要用户名、密码、昵称和邮箱")
    @OperationLog(module = "认证模块", operation = "用户注册")
    @Idempotent(prefix = "register", expireTime = 10, message = "请勿重复注册")
    public Result<Void> register(
            @Parameter(description = "注册请求参数，包含username、password、nickname、email", required = true)
            @Valid @RequestBody RegisterRequest request) {
        authService.register(request.getUsername(), request.getPassword(), 
                           request.getNickname(), request.getEmail());
        return Result.success("注册成功", null);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户退出登录，需要提供用户ID")
    @OperationLog(module = "认证模块", operation = "用户登出")
    public Result<Void> logout(
            @Parameter(description = "用户ID，从请求头X-User-Id获取", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        authService.logout(userId);
        return Result.success("登出成功", null);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/userinfo")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户的详细信息")
    public Result<User> getUserInfo(
            @Parameter(description = "用户ID，从请求头X-User-Id获取", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        User user = authService.getUserInfo(userId);
        return Result.success(user);
    }

    /**
     * 验证Token
     */
    @PostMapping("/validate")
    @Operation(summary = "验证Token", description = "验证JWT Token是否有效")
    public Result<Boolean> validateToken(
            @Parameter(description = "要验证的JWT Token", required = true)
            @RequestBody Map<String, String> request) {
        String token = request.get("token");
        boolean valid = authService.validateToken(token);
        return Result.success(valid);
    }
}
