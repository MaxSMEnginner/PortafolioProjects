package com.maxacm.lr.controller;

import com.maxacm.lr.entity.AuditLog;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.UserRepository;
import com.maxacm.lr.service.AuditLogService;
import com.maxacm.lr.service.UserService;
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

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
        return userRepository.findById(id).map(user -> {
            dto.username().ifPresent(user::setUsername);
            dto.password().ifPresent(p -> user.setPassword(passwordEncoder().encode(p)));
            dto.role().ifPresent(user::setRole);
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Eliminar usuario
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
