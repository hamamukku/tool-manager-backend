// 🧠 serviceブロック：ビジネスロジック層（登録・ログイン）

package com.example.toolmanager.service;

import com.example.toolmanager.dto.UserRequest;
import com.example.toolmanager.entity.UserEntity;
import com.example.toolmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.toolmanager.security.JwtProvider;
import java.util.Optional;




@Service
public class UserService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
public UserService(UserRepository userRepository,
                   PasswordEncoder passwordEncoder,
                   JwtProvider jwtProvider) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtProvider = jwtProvider;}
  

    public ResponseEntity<?> register(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    public ResponseEntity<?> login(UserRequest request) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty() || !passwordEncoder.matches(
                request.getPassword(),
                optionalUser.get().getPassword())
        ) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // 本来ここでJWTを生成して返す（仮のレスポンスでOK）
        return ResponseEntity.ok("Login successful");
    }
}
