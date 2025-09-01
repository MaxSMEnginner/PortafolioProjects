package com.maxacm.lr.controller;

import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.UserRepository;
import com.maxacm.lr.service.UserService;
import com.maxacm.lr.dto.UserUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Crear usuario (usando UserService para encriptar password)
    @PostMapping("/register")
    public ResponseEntity<User> create(@RequestBody User user) {
        User saved = userService.register(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(saved);
    }




}
