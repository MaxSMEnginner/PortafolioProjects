package com.maxacm.lr.controller;

import com.maxacm.lr.entity.AuditLog;
import com.maxacm.lr.entity.BlacklistedToken;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.UserRepository;
import com.maxacm.lr.service.AuditLogService;
import com.maxacm.lr.service.UserService;
import com.maxacm.lr.service.TokenBlacklistService;
import com.maxacm.lr.dto.UserUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AuditLogService auditLogService;
    private final TokenBlacklistService blacklistService;

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @GetMapping("/black-list")
    public List<BlacklistedToken> getblAll(){
        return blacklistService.getAll();

    }

    // ✅ Ver todos los logs
    @GetMapping("/audit-log")
    public List<AuditLog> getAll() {
        return auditLogService.getAll();
    }

    // ✅ Filtrar logs por usuario
    @GetMapping("/audit-log/user/{username}")
    public List<AuditLog> getByUser(@PathVariable String username) {
        return auditLogService.getByUser(username);
    }

    // ✅ Filtrar logs por acción (LOGIN, REFRESH, LOGOUT)
    @GetMapping("/audit-log/action/{action}")
    public List<AuditLog> getByAction(@PathVariable String action) {
        return auditLogService.getByAction(action);
    }

    // ✅ Home para ADMIN
    @GetMapping("/home")
    public String admin() {
        return "Bienvenido ADMIN 👑";
    }

    // ✅ Crear usuario ADMIN
    @PostMapping("/registeradmin")
    public ResponseEntity<User> create(@RequestBody User user) {
        User saved = userService.registeradmin(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/register")
    public ResponseEntity<User> createuser(@RequestBody User user) {
        User saved = userService.register(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(saved);
    }


    // ✅ Listar todos los usuarios
    @GetMapping("/users")
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ✅ Buscar usuario por ID
    @GetMapping("/user/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Actualizar usuario parcialmente
    @PatchMapping("/user/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        try{
            User updateuser= userService.updateUSERadmin(id, dto);
            return ResponseEntity.ok(updateuser);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }

    }

    // ✅ Eliminar usuario
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
