package com.stock.user.controller;

import com.stock.common.result.Result;
import com.stock.user.entity.User;
import com.stock.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<?> register(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam(value = "nickName", required = false) String nickName) {
        return userService.register(username, password, nickName);
    }

    @PostMapping("/login")
    public Result<?> login(@RequestParam("username") String username,
                            @RequestParam("password") String password) {
        return userService.login(username, password);
    }

    @GetMapping("/info")
    public Result<User> info(@RequestParam("username") String username) {
        return Result.success(userService.getByUsername(username));
    }
}
