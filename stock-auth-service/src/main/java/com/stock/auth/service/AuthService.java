package com.stock.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stock.auth.entity.User;
import com.stock.auth.repository.UserMapper;
import com.stock.auth.security.JwtUtil;
import com.stock.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TOKEN_PREFIX = "token:";

    /**
     * 用户登录
     */
    public Map<String, Object> login(String username, String password) {
        try {
            // 查询用户
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            User user = userMapper.selectOne(wrapper);

            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 验证密码（使用BCrypt）
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BusinessException("密码错误");
            }

            // 检查用户状态
            if (user.getStatus() == 0) {
                throw new BusinessException("用户已被禁用");
            }

            // 生成Token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());

            // 将Token存入Redis
            try {
                String redisKey = TOKEN_PREFIX + user.getId();
                redisTemplate.opsForValue().set(redisKey, token, 24, TimeUnit.HOURS);
            } catch (Exception e) {
                System.err.println("Redis连接失败: " + e.getMessage());
                e.printStackTrace();
                // 即使Redis失败，也允许登录，只是无法实现单点登录或强制下线等功能
                // 或者选择抛出异常
                throw new BusinessException("系统内部错误：Redis服务不可用");
            }

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("nickname", user.getNickname());

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("登录过程发生未知错误: " + e.getMessage());
            e.printStackTrace();
            throw new BusinessException("登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    public void register(String username, String password, String nickname, String email) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        Long count = userMapper.selectCount(wrapper);

        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setEmail(email);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
    }

    /**
     * 用户登出
     */
    public void logout(Long userId) {
        String redisKey = TOKEN_PREFIX + userId;
        redisTemplate.delete(redisKey);
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            return false;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String redisKey = TOKEN_PREFIX + userId;
        String cachedToken = redisTemplate.opsForValue().get(redisKey);

        return token.equals(cachedToken);
    }

    /**
     * 获取用户信息
     */
    public User getUserInfo(Long userId) {
        return userMapper.selectById(userId);
    }

}