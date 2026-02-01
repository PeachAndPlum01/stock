package com.stock.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stock.auth.entity.User;
import com.stock.auth.mapper.UserMapper;
import com.stock.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
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
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_PREFIX = "token:";

    /**
     * 用户登录
     */
    public Map<String, Object> login(String username, String password) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        String encryptedPassword = encryptPassword(password);
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 将Token存入Redis
        String redisKey = TOKEN_PREFIX + user.getId();
        redisTemplate.opsForValue().set(redisKey, token, 24, TimeUnit.HOURS);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());

        return result;
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
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptPassword(password));
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
        String cachedToken = (String) redisTemplate.opsForValue().get(redisKey);

        return token.equals(cachedToken);
    }

    /**
     * 获取用户信息
     */
    public User getUserInfo(Long userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 密码加密
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
    }
}
