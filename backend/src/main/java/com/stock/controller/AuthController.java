package com.stock.controller;

import com.stock.common.Result;
import com.stock.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            log.debug("收到登录请求: username={}", username);

            if (username == null || username.trim().isEmpty()) {
                log.warn("登录失败: 用户名为空");
                return Result.error("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                log.warn("登录失败: 密码为空, username={}", username);
                return Result.error("密码不能为空");
            }

            Map<String, Object> result = userService.login(username, password);
            log.info("用户登录成功: username={}", username);
            return Result.success("登录成功", result);
        } catch (Exception e) {
            log.error("用户登录异常: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/userInfo")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            log.debug("收到获取用户信息请求");
            // 这里简化处理，实际应该从token中解析用户名
            // 在实际项目中应该使用拦截器统一处理
            String username = "admin"; // 临时处理
            Map<String, Object> userInfo = userService.getUserInfo(username);
            log.info("获取用户信息成功: username={}", username);
            return Result.success(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        try {
            log.debug("收到用户登出请求");
            // 这里简化处理
            String username = "admin"; // 临时处理
            userService.logout(username);
            log.info("用户登出成功: username={}", username);
            return Result.success("登出成功", null);
        } catch (Exception e) {
            log.error("用户登出失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
