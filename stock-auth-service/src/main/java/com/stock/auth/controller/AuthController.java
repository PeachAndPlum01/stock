package com.stock.auth.controller;

import com.stock.auth.entity.User;
import com.stock.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> login(
            @Parameter(description = "登录请求参数", required = true)
            @RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            Map<String, Object> result = authService.login(username, password);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账号，需要用户名、密码、昵称和邮箱")
    public ResponseEntity<Map<String, Object>> register(
            @Parameter(description = "注册请求参数，包含username、password、nickname、email", required = true)
            @RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String nickname = request.get("nickname");
            String email = request.get("email");

            authService.register(username, password, nickname, email);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "注册成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户退出登录，需要提供用户ID")
    public ResponseEntity<Map<String, Object>> logout(
            @Parameter(description = "用户ID，从请求头X-User-Id获取", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        try {
            authService.logout(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "登出成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/userinfo")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户的详细信息")
    public ResponseEntity<Map<String, Object>> getUserInfo(
            @Parameter(description = "用户ID，从请求头X-User-Id获取", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        try {
            User user = authService.getUserInfo(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", user);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 验证Token
     */
    @PostMapping("/validate")
    @Operation(summary = "验证Token", description = "验证JWT Token是否有效")
    public ResponseEntity<Map<String, Object>> validateToken(
            @Parameter(description = "要验证的JWT Token", required = true)
            @RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            boolean valid = authService.validateToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "验证完成");
            response.put("data", valid);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
