package com.maxacm.lr.controller;

import com.maxacm.lr.entity.User;
import com.maxacm.lr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    // ✅ Crear usuario (usando UserService para encriptar password)
    @PostMapping("/register")
    public ResponseEntity<User> create(@RequestBody User user) {
        User saved = userService.register(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/home")
    public String home() {
        return "Bienvenido USER 🚀";
    }



}
