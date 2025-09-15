package com.maxacm.lr.controller;

import com.maxacm.lr.dto.UserUpdateDTOUSER;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.maxacm.lr.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;


    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


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

    @PatchMapping("/user/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserUpdateDTOUSER dto) {
        try{
            User updateuser= userService.updateUSER(id, dto);
            return ResponseEntity.ok(updateuser);
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getuserByUsername(@PathVariable String username){
        try{
            User user= userService.findByUsername(username);
            return ResponseEntity.ok(user);
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    //Es lo mismo a lo de arriba
//    @GetMapping("/users/username/{username}")
//    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
//        return userService.findByUsername(username)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }



}
