package com.stock.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stock.common.utils.JwtUtils;
import com.stock.common.result.Result;
import com.stock.user.entity.User;
import com.stock.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${jwt.secret:stock-platform-secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public User getByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return getBaseMapper().selectOne(wrapper);
    }

    public Result<?> register(String username, String password, String nickName) {
        if (getByUsername(username) != null) {
            return Result.error("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setNickName(nickName);
        user.setStatus(1);
        save(user);
        return Result.success("注册成功");
    }

    public Result<?> login(String username, String password) {
        User user = getByUsername(username);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        String token = JwtUtils.generateToken(user.getUsername());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("nickName", user.getNickName());
        return Result.success("登录成功", data);
    }
}
