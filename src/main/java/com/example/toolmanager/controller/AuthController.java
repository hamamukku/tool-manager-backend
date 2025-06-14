// 🎮 controllerブロック：認証系のエンドポイント管理

package com.example.toolmanager.controller;

import com.example.toolmanager.dto.UserRequest;
import com.example.toolmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 🔐 ユーザー登録API
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest request) {
        return userService.register(request);
    }

    // 🔐 ログインAPI
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest request) {
        return userService.login(request);
    }
}
