package com.maxacm.lr.controller;

import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.UserRepository;
import com.maxacm.lr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    // 👉 pequeño helper porque aquí no inyectamos PasswordEncoder
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    // ✅ Crear usuario (usando UserService para encriptar password)
    @PostMapping
    public ResponseEntity<User> create(@RequestParam String username, @RequestParam String password) {
        User saved = userService.register(username, password);
        return ResponseEntity.ok(saved);
    }

    // ✅ Listar todos los usuarios
    @GetMapping
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ✅ Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Actualizar usuario (solo username por ejemplo)
    @PatchMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User updated) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updated.getUsername());
            if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder().encode(updated.getPassword()));
            }
            user.setRole(updated.getRole());
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}
